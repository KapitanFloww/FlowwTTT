package de.flowwindustries.flowwttt.repository;

import de.flowwindustries.flowwttt.config.FileConfigurationWrapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Optional;
import java.util.Properties;

import static de.flowwindustries.flowwttt.config.DefaultConfiguration.PATH_DATASOURCE_JDBC_DRIVER;
import static de.flowwindustries.flowwttt.config.DefaultConfiguration.PATH_DATASOURCE_JDBC_PASSWORD;
import static de.flowwindustries.flowwttt.config.DefaultConfiguration.PATH_DATASOURCE_JDBC_URL;
import static de.flowwindustries.flowwttt.config.DefaultConfiguration.PATH_DATASOURCE_JDBC_USERNAME;
import static de.flowwindustries.flowwttt.config.DefaultConfiguration.PATH_HIBERNATE_CONNECTION_PROVIDER;
import static de.flowwindustries.flowwttt.config.DefaultConfiguration.PATH_HIBERNATE_DDL_AUTO;
import static de.flowwindustries.flowwttt.config.DefaultConfiguration.PATH_HIBERNATE_DIALECT;
import static de.flowwindustries.flowwttt.config.DefaultConfiguration.PATH_HIBERNATE_SHOW_SQL;

/**
 * Abstract class to provide easy functionality to access the persistence layer.
 * @param <E> the entity class
 * @param <I> the identifier class
 */
@RequiredArgsConstructor
public abstract class AbstractRepository<E, I> {

    private final Class<E> entityClass;
    private final FileConfigurationWrapper fileConfigurationWrapper;
    private static EntityManagerFactory entityManagerFactory;

    /**
     * Create and persist an entity.
     * @param entity the entity to persist.
     * @return the persisted entity
     */
    @Transactional
    public synchronized E create(E entity) {
        EntityManager entityManager = getEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        entityManager.persist(entity);

        entityTransaction.commit();
        entityManager.close();
        return entity;
    }

    /**
     * Edit and persisted entity.
     * @param entity the updated entity to persist
     * @return the updated entity
     */
    @Transactional
    public synchronized E edit(E entity) {
        EntityManager entityManager = getEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        entityManager.merge(entity);

        entityTransaction.commit();
        entityManager.close();
        return entity;
    }

    /**
     * Remove a persistent entity.
     * @param entity the entity to remove
     */
    @Transactional
    public synchronized void remove(E entity) {
        EntityManager entityManager = getEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        entityManager.remove(entityManager.merge(entity));

        entityTransaction.commit();
        entityManager.close();
    }

    @Transactional
    public synchronized void removeAll() {
        EntityManager entityManager = getEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        var entities = findAll();
        entities.forEach(this::remove);

        entityTransaction.commit();
        entityManager.close();
    }

    /**
     * Find a persistent entity.
     * @param id id of the
     * @return the found entity inside an {@link Optional} or {@link Optional#empty()}
     */
    public synchronized Optional<E> find(I id) {
        EntityManager entityManager = getEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        E e = entityManager.find(entityClass, id);

        entityTransaction.commit();
        entityManager.close();
        if (e == null) {
            return Optional.empty();
        }
        return Optional.of(e);
    }

    /**
     * Find all persistent entities.
     * @return all entities
     */
    public synchronized Collection<E> findAll() {
        EntityManager entityManager = getEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        CriteriaQuery<E> cq = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
        cq.select(cq.from(entityClass));
        Collection<E> e = getEntityManager().createQuery(cq).getResultList();

        entityTransaction.commit();
        entityManager.close();
        return e;
    }

    /**
     * Count all stored entities.
     * @return the number of stored entities
     */
    public long count() {
        return findAll().size();
    }

    /**
     * Get the {@link EntityManager}.
     * @return the {@link EntityManager}
     * @throws RuntimeException if the {@link EntityManager} is not yet initialized
     */
    protected EntityManager getEntityManager() throws RuntimeException {
        setupEntityManagerFactory();
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            return entityManagerFactory.createEntityManager();
        } else {
            throw new RuntimeException("Entity Manager not initialized!");
        }
    }

    private void setupEntityManagerFactory() {
        if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {
            var showSQL = fileConfigurationWrapper.readBoolean(PATH_HIBERNATE_SHOW_SQL);
            var ddlAuto = fileConfigurationWrapper.readString(PATH_HIBERNATE_DDL_AUTO);
            var hibernateDialect = fileConfigurationWrapper.readString(PATH_HIBERNATE_DIALECT);
            var connectionProviderClass = fileConfigurationWrapper.readString(PATH_HIBERNATE_CONNECTION_PROVIDER);

            Properties properties = new Properties();
            properties.put("hibernate.show_sql", showSQL);
            properties.put("hibernate.hbm2ddl.auto", ddlAuto);
            properties.put("hibernate.connection.provider_class", connectionProviderClass);
            properties.put("hibernate.dialect", hibernateDialect);

            var jdbcDriver = fileConfigurationWrapper.readString(PATH_DATASOURCE_JDBC_DRIVER);
            var jdbcUrl = fileConfigurationWrapper.readString(PATH_DATASOURCE_JDBC_URL);
            var databaseUsername = fileConfigurationWrapper.readString(PATH_DATASOURCE_JDBC_USERNAME);
            var databasePassword = fileConfigurationWrapper.readString(PATH_DATASOURCE_JDBC_PASSWORD);

            properties.put("jakarta.persistence.jdbc.driver", jdbcDriver);
            properties.put("jakarta.persistence.jdbc.url", jdbcUrl);
            properties.put("jakarta.persistence.jdbc.user", databaseUsername);
            properties.put("jakarta.persistence.jdbc.password", databasePassword);

            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            entityManagerFactory = Persistence.createEntityManagerFactory("persistence-unit", properties);
        }
    }

    public static void closeEntityManagerFactory() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }
}

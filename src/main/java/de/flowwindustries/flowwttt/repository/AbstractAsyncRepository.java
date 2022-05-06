package de.flowwindustries.flowwttt.repository;

import de.flowwindustries.flowwttt.repository.callbacks.AsyncCallbackExceptionHandler;
import de.flowwindustries.flowwttt.repository.callbacks.AsyncCallbackObject;
import de.flowwindustries.flowwttt.repository.callbacks.AsyncCallbackObjectCollection;

/**
 * Abstract class to provide easy functionality to access the persistence layer (asynchronously).
 * @param <E> the entity class
 * @param <I> the identifier class
 */
public abstract class AbstractAsyncRepository<E, I> extends AbstractRepository<E, I> {

    /**
     * Constructor
     */
    public AbstractAsyncRepository(Class<E> entityClass) {
        super(entityClass);
    }

    /**
     * Remove a persisted entity.
     * @param entity the entity to remove
     * @param exceptionHandler error handler callback
     */
    public void remove(E entity, AsyncCallbackExceptionHandler exceptionHandler) {
        new Thread(() -> {
            try {
                super.remove(entity);
            } catch (RuntimeException e) {
                exceptionHandler.error(e);
            }
        }).start();
    }

    /**
     * Find a persisted entity.
     * @param id the identifier of the entity
     * @param callback success callback
     * @param exceptionHandler error handler callback
     */
    public void find(I id, AsyncCallbackObject<E> callback, AsyncCallbackExceptionHandler exceptionHandler) {
        new Thread(() -> {
            try {
                callback.done(super.find(id));
            } catch (RuntimeException e) {
                exceptionHandler.error(e);
            }
        }).start();
    }

    /**
     * Find all persisted entities.
     * @param callback success callback
     * @param exceptionHandler error handler callback
     */
    public void findAll(AsyncCallbackObjectCollection<E> callback, AsyncCallbackExceptionHandler exceptionHandler) {
        new Thread(() -> {
            try {
                callback.done(super.findAll());
            } catch (RuntimeException e) {
                exceptionHandler.error(e);
            }
        }).start();
    }

    /**
     * Edit a persistent entity.
     * @param entity the entity to edit
     * @param callback success callback
     * @param exceptionHandler error handler callback
     */
    public synchronized void edit(E entity, AsyncCallbackObject<E> callback, AsyncCallbackExceptionHandler exceptionHandler) {
        new Thread(() -> {
            try {
                callback.done(super.edit(entity));
            } catch (RuntimeException e) {
                exceptionHandler.error(e);
            }
        }).start();
    }

    /**
     * Create a persistent entity.
     * @param entity the entity to persist
     * @param exceptionHandler error handler callback
     */
    public void create(E entity, AsyncCallbackExceptionHandler exceptionHandler) {
        new Thread(() -> {
            try {
                super.create(entity);
            } catch (RuntimeException e) {
                exceptionHandler.error(e);
            }
        }).start();
    }
}

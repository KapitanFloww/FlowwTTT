package de.flowwindustries.flowwttt.domain;

/**
 * Interface to declare an entity as being identifiable using a unique identifier.
 * @param <I> the identifier class type
 */
public interface Identifiable<I> {

    /**
     * Return the unique identifier for this entity.
     * @return the unique identifier
     */
    I getId();
}

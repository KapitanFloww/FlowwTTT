package de.flowwindustries.flowwttt.domain.locations;

import de.flowwindustries.flowwttt.domain.Identifiable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

/**
 * Spawn point of chests, located in an arena.
 */
@Data
@Entity
public class ChestSpawn implements Identifiable<Integer> {

    /**
     * Database identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    /**
     * X coordinate.
     */
    private Double x;

    /**
     * Y coordinate.
     */
    private Double y;

    /**
     * Z coordinate.
     */
    private Double z;

    /**
     * World name.
     */
    private String worldName;
}

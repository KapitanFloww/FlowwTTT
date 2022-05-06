package de.flowwindustries.flowwttt.domain.locations;

import de.flowwindustries.flowwttt.domain.Identifiable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

/**
 * Serializable spawn point entity.
 */
@Data
@Entity
public class PlayerSpawn implements Identifiable<Integer> {

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
     * Players yaw.
     */
    private Float yaw;

    /**
     * Players pitch.
     */
    private Float pitch;

    /**
     * World name.
     */
    private String worldName;
}

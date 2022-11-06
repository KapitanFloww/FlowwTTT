package de.flowwindustries.flowwttt.domain.locations;

import de.flowwindustries.flowwttt.domain.Identifiable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

/**
 * Serializable spawn point entity.
 */
@Data
@With
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "player_spawns")
public class PlayerSpawn implements Identifiable<Integer> {

    /**
     * Database identifier.
     */
    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    /**
     * X coordinate.
     */
    @Column(name = "x", nullable = false)
    private Double x;

    /**
     * Y coordinate.
     */
    @Column(name = "y", nullable = false)
    private Double y;

    /**
     * Z coordinate.
     */
    @Column(name = "z", nullable = false)
    private Double z;

    /**
     * World name.
     */
    @Column(name = "world_name", nullable = false)
    private String worldName;

    /**
     * Players yaw.
     */
    @Column(name = "yaw", nullable = false)
    private Float yaw;

    /**
     * Players pitch.
     */
    @Column(name = "pitch", nullable = false)
    private Float pitch;
}

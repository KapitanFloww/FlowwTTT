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
 * Spawn point of chests, located in an arena.
 */
@Data
@With
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chest_spawn")
public class ChestSpawn implements Identifiable<Integer> {

    /**
     * Database identifier.
     */
    @Id
    @Column(name = "id", nullable = false, unique = true)
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
}

package de.flowwindustries.flowwttt.domain.locations;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.List;

/**
 * Entity for arenas.
 */
@Data
@With
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "arenas")
public class Arena {

    /**
     * Arena name.
     */
    @Id
    @Column(name = "arena_name", nullable = false, unique = true)
    private String arenaName;

    /**
     * SpawnPoints of the arena.
     */
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<PlayerSpawn> playerSpawns;

    /**
     * Chest spawn points of the arena.
     */
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<ChestSpawn> chestSpawns;

    /**
     * Location of the player tester.
     */
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private PlayerTester playerTester;
}

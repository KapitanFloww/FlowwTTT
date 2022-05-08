package de.flowwindustries.flowwttt.domain.locations;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;

import java.util.Collection;

/**
 * Entity for arenas.
 */
@Data
@Entity
public class Arena {

    /**
     * Arena name.
     */
    @Id
    private String arenaName;

    /**
     * SpawnPoints of the arena.
     */
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Collection<PlayerSpawn> playerSpawns;

    /**
     * Chest spawn points of the arena.
     */
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Collection<ChestSpawn> chestSpawns;

    /**
     * Location of the player tester.
     */
    @OneToOne
    private PlayerTester playerTester;
}

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
 * Lobby entity
 */
@Data
@Entity
public class Lobby {

    @Id
    private String lobbyName;

    /**
     * Lobby Spawn Point.
     */
    @OneToOne(fetch = FetchType.EAGER, cascade =  CascadeType.ALL)
    private PlayerSpawn lobbySpawn;

    /**
     * Playable arenas of a lobby.
     */
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Collection<Arena> arenas;
}

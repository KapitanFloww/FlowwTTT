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

import java.util.Collection;

/**
 * Lobby entity
 */
@Data
@With
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lobbies")
public class Lobby {

    @Id
    @Column(name = "lobbyName", unique = true, nullable = false)
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

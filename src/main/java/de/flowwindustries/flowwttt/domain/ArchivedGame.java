package de.flowwindustries.flowwttt.domain;

import de.flowwindustries.flowwttt.domain.enumeration.GameResult;
import de.flowwindustries.flowwttt.domain.enumeration.Stage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.Instant;

/**
 * Entity to persist archived games.
 */
@Data
@With
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "archived_games")
public class ArchivedGame {

    /**
     * Unique game id.
     */
    @Id
    @Column(name = "instance_id", unique = true, nullable = false)
    private String instanceId;

    /**
     * Game result.
     */
    @Column(name = "game_result")
    @Enumerated(EnumType.STRING)
    private GameResult gameResult;

    /**
     * Stage.
     */
    @Column(name = "stage", nullable = false)
    private Stage stage;

    /**
     * Lobby name.
     */
    @Column(name = "lobby_name", nullable = false)
    private String lobbyName;

    /**
     * Arena name.
     */
    @Column(name = "arena_name")
    private String arenaName;

    /**
     * Player names.
     */
    @Column(name = "player_names")
    private String playerNames;

    /**
     * Timestamp.
     */
    @Column(name = "ended_at")
    private Instant endedAt;
}


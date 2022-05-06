package de.flowwindustries.flowwttt.domain;

import de.flowwindustries.flowwttt.domain.enumeration.GameResult;
import de.flowwindustries.flowwttt.domain.enumeration.Stage;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * Entity to persist archived games.
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ArchivedGame {

    /**
     * Unique game id.
     */
    @Id
    private String instanceId;

    /**
     * Game result.
     */
    private GameResult gameResult;

    /**
     * Stage.
     */
    private Stage stage;

    /**
     * Lobby name.
     */
    private String lobbyName;

    /**
     * Arena name.
     */
    private String arenaName;

    /**
     * Player names.
     */
    @ElementCollection
    private List<String> playerNames;

    /**
     * Timestamp.
     */
    private Instant endedAt;
}

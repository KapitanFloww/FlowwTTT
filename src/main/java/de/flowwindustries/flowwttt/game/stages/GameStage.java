package de.flowwindustries.flowwttt.game.stages;

import de.flowwindustries.flowwttt.domain.enumeration.Stage;

/**
 * GameStage is a portion of the match.
 * Each stage may define different {@link #beginStage()} and {@link #endStage()} logic.
 */
public interface GameStage {

    /**
     * Get the name of the current stage.
     * @return - the name of this stage
     */
    Stage getName();

    /**
     * Get the next {@link Stage} following the lifecycle's sequence.
     * @return - the next stage
     */
    Stage getNext();

    /**
     * Begin this stage.
     */
    void beginStage();

    /**
     * End this stage.
     */
    void endStage();
}

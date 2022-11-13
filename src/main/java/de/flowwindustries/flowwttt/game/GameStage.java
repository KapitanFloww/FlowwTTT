package de.flowwindustries.flowwttt.game;

import de.flowwindustries.flowwttt.domain.enumeration.Stage;

public interface GameStage {

    Stage getName();

    Stage getNext();

    void beginStage();

    void endStage();
}

package de.flowwindustries.flowwttt.repository;

import de.flowwindustries.flowwttt.PluginContextTest;
import de.flowwindustries.flowwttt.PluginContextTestExtension;
import de.flowwindustries.flowwttt.domain.locations.Lobby;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@PluginContextTest
public class LobbyRepositoryTest {

    private LobbyRepository lobbyRepository;

    @BeforeEach
    void setUp() {
        var context = PluginContextTestExtension.getPluginContext();
        lobbyRepository = new LobbyRepository(Lobby.class, context.getConfigurationWrapper());
    }

    @Test
    void createLobbyShouldPersistLobby() {
        // GIVEN
        long count = lobbyRepository.count();
        // WHEN
        lobbyRepository.create(getDummyLobby());
        // THEN
        assertThat(lobbyRepository.count()).isEqualTo(count + 1);
    }

    @Test
    void findLobbyShouldFindPersistedLobby() {
        // GIVEN
        var lobby = lobbyRepository.create(getDummyLobby());
        // WHEN
        var persistedLobby = lobbyRepository.find(lobby.getLobbyName());
        // THEN
        assertThat(persistedLobby).isPresent();
        assertThat(persistedLobby.get().getLobbyName()).isEqualTo(lobby.getLobbyName());
        assertThat(persistedLobby.get().getLobbySpawn()).isEqualTo(lobby.getLobbySpawn());
        assertThat(persistedLobby.get().getArenas().size()).isEqualTo(lobby.getArenas().size());
    }

    static Lobby getDummyLobby() {
        return new Lobby()
                .withLobbyName("testlobby")
                .withLobbySpawn(null)
                .withArenas(Collections.emptyList());
    }

    @AfterEach
    void tearDown() {
        lobbyRepository.removeAll();
    }
}

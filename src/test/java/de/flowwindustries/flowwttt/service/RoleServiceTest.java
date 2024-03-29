package de.flowwindustries.flowwttt.service;

import de.flowwindustries.flowwttt.domain.enumeration.Role;
import de.flowwindustries.flowwttt.services.RoleService;
import de.flowwindustries.flowwttt.services.impl.RoleServiceImpl;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Log
public class RoleServiceTest {

    private static final Map<Role, Float> roleRatios = new HashMap<>();
    static {
        roleRatios.put(Role.INNOCENT, 0.60f); // 60%
        roleRatios.put(Role.TRAITOR, 0.30f); // 30%
        roleRatios.put(Role.DETECTIVE, 0.10f); // 10%
    }

    // Unit-under-test
    private final RoleService roleService = new RoleServiceImpl(roleRatios);

    @Test
    void verifyPlayerRolesAssignedEven() {
        var playerList = List.of("Player1", "Player2", "Player3", "Player4", "Player5",
                "Player6", "Player7", "Player8", "Player9", "Player10");
        verifyRoleAssignment(playerList);
    }

    @Test
    void verifyPlayerRolesAssignedUneven() {
        var playerList = List.of("Player1", "Player2", "Player3", "Player4", "Player5",
                "Player6", "Player7", "Player8", "Player9", "Player10", "Player11");
        verifyRoleAssignment(playerList);
    }

    private void verifyRoleAssignment(List<String> playerList) {
        var result = roleService.assignRoles(playerList);
        // Verify correct occurrences
        var innocents = filterRole(result, Role.INNOCENT);
        var detective = filterRole(result, Role.DETECTIVE);
        var traitor = filterRole(result, Role.TRAITOR);

        if(playerList.size() % 2 == 0) {
            assertThat(innocents).hasSize((int) (roleRatios.get(Role.INNOCENT) * playerList.size()));
        } else {
            assertThat(innocents).hasSize((int) (roleRatios.get(Role.INNOCENT) * playerList.size() + 1)); // add stashed member
        }

        assertThat(detective).hasSize((int) (roleRatios.get(Role.DETECTIVE) * playerList.size()));
        assertThat(traitor).hasSize((int) (roleRatios.get(Role.TRAITOR) * playerList.size()));
        // Verify all different names
        assertThat(result.keySet()).hasSize(playerList.size());
    }

    private static List<String> filterRole(Map<String, Role> result, Role role) {
        return result.keySet().stream().filter(it -> result.get(it) == role).toList();
    }
}

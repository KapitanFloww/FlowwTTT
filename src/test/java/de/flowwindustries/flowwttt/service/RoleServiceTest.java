package de.flowwindustries.flowwttt.service;

import de.flowwindustries.flowwttt.services.RoleService;
import de.flowwindustries.flowwttt.services.impl.RoleServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.List;

public class RoleServiceTest {

    private RoleService roleService = new RoleServiceImpl();

    @Test
    void verifyPlayerRolesAssigned() {

        var playerList = List.of("Kapitan_Floww", "saltysnacc", "Caropop", "Lucky_Miner23", "Explosive_Sheep", "FunPixelHD", "Player7", "Player8", "Player9", "Player10");
        var result = roleService.assignRoles(playerList);
        System.out.println(result);
    }
}

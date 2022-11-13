package de.flowwindustries.flowwttt.services.impl;

import de.flowwindustries.flowwttt.domain.enumeration.Role;
import de.flowwindustries.flowwttt.services.RoleService;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.IntStream;

@Log
public class RoleServiceImpl implements RoleService {

    private static final Random rand = new Random();
    private final Map<Role, Float> roleRatios;

    public RoleServiceImpl(Map<Role, Float> roleRations) {
        this.roleRatios  = Objects.requireNonNull(roleRations);
    }

    @Override
    public Map<String, Role> assignRoles(final List<String> players) {
        // Check edge cases
        if (players.size() == 1) {
            return getSinglePlayerAssignment(players);
        } else if (players.size() == 2) {
            return getDuoPlayerAssignment(players);
        } else if (players.size() == 3) {
            return getTrioPlayerAssignment(players);
        }

        Map<String, Role> roleAssignment = new HashMap<>();

        long totalPlayers = players.size();
        List<String> playersWithoutRole = new ArrayList<>(players);
        List<String> playersWithRole = new ArrayList<>();

        if(totalPlayers % 2 == 0) {
            return assignRolesIntern(roleAssignment, totalPlayers, playersWithoutRole, playersWithRole);
        }
        return assignRolesUneven(roleAssignment, totalPlayers, playersWithoutRole, playersWithRole);
    }

    private Map<String, Role> assignRolesIntern(Map<String, Role> roleAssignment, long totalPlayers, List<String> playersWithoutRole, List<String> playersWithRole) {
        // Iterate through all roles
        roleRatios.keySet().forEach(role -> {
            final float roleRatio = roleRatios.get(role);
            final int roleOccurrence = (int) Math.ceil(totalPlayers * roleRatio);

            log.info("Role: %s, Occurrence: %sx (%s)".formatted(role, roleOccurrence, roleRatio));

            // Get a player for each occurrence
            IntStream.range(0, roleOccurrence).forEach(it -> {

                final int randomIndex = getRandomIndex(playersWithoutRole.size(), playersWithRole.size());
                if(playersWithoutRole.size() == 0) {
                    log.info("No players without role left. Skipping");
                    return;
                }
                String randomPlayerName = playersWithoutRole.get(randomIndex);
                log.info("Random index: %s, Player: %s".formatted(randomIndex, randomPlayerName));

                // Update lists
                playersWithRole.add(randomPlayerName);
                playersWithoutRole.remove(randomPlayerName);

                // Save player assignment
                roleAssignment.put(randomPlayerName, role);
            });
        });
        return roleAssignment;
    }

    private Map<String, Role> assignRolesUneven(Map<String, Role> roleAssignment, long totalPlayers, List<String> playersWithoutRole, List<String> playersWithRole) {
        var stashedPlayer = playersWithoutRole.get(rand.nextInt(playersWithoutRole.size() - 1));
        playersWithoutRole.remove(stashedPlayer);
        totalPlayers = totalPlayers - 1;
        log.info("Assigning uneven players. Stashed player: %s".formatted(stashedPlayer));
        var result = assignRolesIntern(roleAssignment, totalPlayers, playersWithoutRole, playersWithRole);
        result.put(stashedPlayer, Role.INNOCENT);
        return result;
    }

    private static int getRandomIndex(int playersWithoutSize, int playersWithSize) {
        int upperBound = playersWithoutSize - playersWithSize;
        if(upperBound > 0) {
            return rand.nextInt(upperBound);
        } else {
            return 0;
        }
    }

    private static Map<String, Role> getSinglePlayerAssignment(List<String> playersWithoutRole) {
        return Map.of(
                playersWithoutRole.get(0), Role.INNOCENT
        );
    }

    private static Map<String, Role> getDuoPlayerAssignment(List<String> playersWithoutRole) {
        return Map.of(
                playersWithoutRole.get(0), Role.INNOCENT,
                playersWithoutRole.get(1), Role.TRAITOR
        );
    }

    private static Map<String, Role> getTrioPlayerAssignment(List<String> playersWithoutRole) {
        return Map.of(
                playersWithoutRole.get(0), Role.INNOCENT,
                playersWithoutRole.get(1), Role.DETECTIVE,
                playersWithoutRole.get(2), Role.DETECTIVE
        );
    }
}



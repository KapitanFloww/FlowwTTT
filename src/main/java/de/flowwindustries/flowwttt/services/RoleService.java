package de.flowwindustries.flowwttt.services;

import de.flowwindustries.flowwttt.domain.enumeration.Role;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public interface RoleService {
    Map<Player, Role> assignRoles(List<Player> players, List<Role> roles, Map<Role, Integer> roleRatios);
}

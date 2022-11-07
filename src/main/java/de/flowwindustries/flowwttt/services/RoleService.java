package de.flowwindustries.flowwttt.services;

import de.flowwindustries.flowwttt.domain.enumeration.Role;

import java.util.List;
import java.util.Map;

public interface RoleService {
    Map<String, Role> assignRoles(List<String> playerNames);
}

package de.flowwindustries.flowwttt.services.impl;

import de.flowwindustries.flowwttt.domain.enumeration.Role;
import de.flowwindustries.flowwttt.services.RoleService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RoleServiceImpl implements RoleService {

    private static final Random rand = new Random();
    private final Map<Role, Float> roleRatios = new HashMap<>();

    public RoleServiceImpl() {
        this.roleRatios.put(Role.INNOCENT, 60.0f); // 60%
        this.roleRatios.put(Role.TRAITOR, 30.0f); // 30%
        this.roleRatios.put(Role.DETECTIVE, 10.0f); // 10%
    }

    @Override
    public Map<String, Role> assignRoles(List<String> players) {

        int nPlayersRemaining = players.size();

        List<String> remainingPlayers = new ArrayList<>(players);

        Random rand = new Random();

        Map<String, Role> roleAssignment = new HashMap<>();

        for(Role role : roleRatios.keySet())
        {
           float roleRatio = roleRatios.get(role);

           int nPlayersTotal = players.size();

           int playersInRole = (int) Math.ceil( nPlayersTotal * roleRatio);

           if(nPlayersRemaining - playersInRole >= 0){
               nPlayersRemaining -= playersInRole;
           }
           else{
               playersInRole = nPlayersRemaining;
           }

           for(int i = 0; i< playersInRole; i++){

               int randomIndex = rand.nextInt(remainingPlayers.size());
               String randomPlayerName = remainingPlayers.get(randomIndex);
               remainingPlayers.remove(randomIndex);

               roleAssignment.put(randomPlayerName, role);

           }


        }
        return roleAssignment;
    }
}

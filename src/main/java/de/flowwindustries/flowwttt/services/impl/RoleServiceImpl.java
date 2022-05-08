package de.flowwindustries.flowwttt.services.impl;

import de.flowwindustries.flowwttt.domain.enumeration.Role;
import de.flowwindustries.flowwttt.services.RoleService;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RoleServiceImpl implements RoleService {


    @Override
    public Map<Player, Role> assignRoles(List<Player> players, List<Role> roles, Map<Role, Integer> roleRatios) {

        int nPlayersRemaining = players.size();

        List<Player> remainingPlayers = players;

        Random rand = new Random();

        Map<Player, Role> roleAssignment = new HashMap<>();

        for(Role role : roles)
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
               Player randomPlayer = remainingPlayers.get(randomIndex);
               remainingPlayers.remove(randomIndex);

               roleAssignment.put(randomPlayer, role);

           }


        }
        return roleAssignment;
    }
}

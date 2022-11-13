package de.flowwindustries.flowwttt.commands.debug;

import de.flowwindustries.flowwttt.commands.AbstractCommand;
import de.flowwindustries.flowwttt.commands.PlayerMessage;
import de.flowwindustries.flowwttt.services.GameManagerService;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Command class for {@code /tasks}
 */
@Log
public class RunningTasksCommand extends AbstractCommand {

    public RunningTasksCommand(String permission) {
        super(permission);
    }

    @Override
    protected boolean playerCommand(Player player, String[] args) {
        if(args.length != 0) {
            throw new IllegalArgumentException("Usage: /tasks");
        }
        var taskIds = GameManagerService.instancesTaskMap;
        PlayerMessage.info("Instance Tasks:", player);
        PlayerMessage.info(taskIds.toString(), player);
        return false;
    }

    @Override
    protected boolean consoleCommand(String[] args) {
        if(args.length != 0) {
            throw new IllegalArgumentException("Usage: /tasks");
        }
        var taskIds = GameManagerService.instancesTaskMap;
        Bukkit.getConsoleSender().sendMessage("Instance Tasks:");
        Bukkit.getConsoleSender().sendMessage(taskIds.toString());
        return false;
    }
}

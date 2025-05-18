package org.iwich.towerdefense.manager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandManager implements CommandExecutor {
    private final GameManager gameManager;

    public CommandManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Только игроки могут использовать эту команду!");
            return true;
        }

        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("tdstart")) {
            gameManager.startGame(player);
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("tdstop")) {
            gameManager.stopGame(player);
            return true;
        }

        return false;
    }
}

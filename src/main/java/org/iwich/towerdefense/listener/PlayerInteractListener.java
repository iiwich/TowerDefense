package org.iwich.towerdefense.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.iwich.towerdefense.data.TowerData;
import org.iwich.towerdefense.manager.GameManager;
import org.iwich.towerdefense.menu.TowerMenu;
import org.iwich.towerdefense.model.PlayerData;

public class PlayerInteractListener implements Listener {
    private final GameManager gameManager;

    public PlayerInteractListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // Открытие меню башен
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = event.getItem();
            if (item != null && item.getType() == Material.PAPER) {
                new TowerMenu(gameManager).open(player);
                event.setCancelled(true);
                return;
            }
        }

        // Установка башни
        if (event.getAction() == Action.LEFT_CLICK_BLOCK && gameManager.isGameActive()) {
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock == null) return;

            PlayerData playerData = gameManager.getPlayerData(player.getUniqueId());
            if (playerData == null) return;

            TowerData selectedTower = playerData.getSelectedTower();
            if (selectedTower != null) {
                boolean success = gameManager.placeTower(player, selectedTower, clickedBlock.getLocation().add(0, 1, 0));
                if (success) {
                    playerData.setSelectedTower(null); // Сбрасываем выбранную башню
                }
                event.setCancelled(true);
            }
        }
    }
}

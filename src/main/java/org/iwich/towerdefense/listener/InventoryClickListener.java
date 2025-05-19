package org.iwich.towerdefense.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.iwich.towerdefense.data.TowerData;
import org.iwich.towerdefense.manager.GameManager;
import org.iwich.towerdefense.model.PlayerData;

public class InventoryClickListener implements Listener {
    private final GameManager gameManager;

    public InventoryClickListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!event.getView().getTitle().equals("Выберите башню")) return;

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        PlayerData playerData = gameManager.getPlayerData(player.getUniqueId());
        if (playerData == null) return;

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        int slot = event.getRawSlot();
        if (slot >= 0 && slot < gameManager.getTowerTypes().size()) {
            TowerData selected = gameManager.getTowerTypes().get(slot);
            playerData.setSelectedTower(selected);
            player.sendMessage("§aВыбрана башня: §e" + selected.getName());
            player.closeInventory();
        }
    }
}
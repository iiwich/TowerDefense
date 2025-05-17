package org.iwich.towerdefense;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class TowerMenu {
    private final GameManager gameManager;

    public TowerMenu(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void open(Player player) {
        try {
            // Создаем инвентарь
            Inventory inv = Bukkit.createInventory(null, 9, "Выберите башню");

            // Заполняем меню
            for (int i = 0; i < Math.min(9, gameManager.getTowerTypes().size()); i++) {
                TowerType towerType = gameManager.getTowerTypes().get(i);
                inv.setItem(i, createTowerItem(towerType));
            }

            player.openInventory(inv);
        } catch (Exception e) {
            player.sendMessage("§cОшибка при открытии меню башен!");
            e.printStackTrace();
        }
    }

    private ItemStack createTowerItem(TowerType towerType) {
        // Создание предмета
        Material material = towerType.getMaterial() != null ?
                towerType.getMaterial() : Material.STONE;
        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§a" + (towerType.getName() != null ? towerType.getName() : "Башня"));

            meta.setLore(Arrays.asList(
                    "§7Урон: §e" + towerType.getDamage(),
                    "§7Скорость: §e" + towerType.getAttackSpeed() + "/сек",
                    "§7Дальность: §e" + towerType.getRange(),
                    "§6Цена: §e" + towerType.getCost(),
                    "",
                    "§aЛКМ по блоку для установки"
            ));

            item.setItemMeta(meta);
        }

        return item;
    }
}
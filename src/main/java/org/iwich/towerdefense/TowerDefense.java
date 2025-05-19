package org.iwich.towerdefense;

import org.bukkit.plugin.java.JavaPlugin;
import org.iwich.towerdefense.effect.PoisonEffect;
import org.iwich.towerdefense.listener.InventoryClickListener;
import org.iwich.towerdefense.listener.PlayerInteractListener;
import org.iwich.towerdefense.manager.CommandManager;
import org.iwich.towerdefense.manager.ConfigManager;
import org.iwich.towerdefense.manager.GameManager;
import org.iwich.towerdefense.model.Tower;

public class TowerDefense extends JavaPlugin {
    public static TowerDefense plugin;

    @Override
    public void onEnable() {
        plugin = this;

        // Инициализация менеджеров
        ConfigManager configManager = new ConfigManager(this);
        GameManager gameManager = new GameManager(this, configManager);

        // Регистрация команд и листенеров
        getCommand("tdstart").setExecutor(new CommandManager(gameManager));
        getCommand("tdstop").setExecutor(new CommandManager(gameManager));
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(gameManager), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(gameManager), this);

        // Загрузка конфигов
        configManager.loadConfig();

        getLogger().info("TowerDefense enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("TowerDefense disabled!");
    }

    public static TowerDefense get() {
        return plugin;
    }

}


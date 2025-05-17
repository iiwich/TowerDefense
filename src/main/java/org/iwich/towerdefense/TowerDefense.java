package org.iwich.towerdefense;

import org.bukkit.plugin.java.JavaPlugin;
import org.iwich.towerdefense.listeners.EventListener;
import org.iwich.towerdefense.managers.CommandManager;
import org.iwich.towerdefense.managers.ConfigManager;
import org.iwich.towerdefense.managers.GameManager;

public class TowerDefense extends JavaPlugin {
    private GameManager gameManager;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        // Инициализация менеджеров
        configManager = new ConfigManager(this);
        gameManager = new GameManager(this, configManager);

        // Регистрация команд и листенеров
        getCommand("tdstart").setExecutor(new CommandManager(gameManager));
        getCommand("tdstop").setExecutor(new CommandManager(gameManager));
        getServer().getPluginManager().registerEvents(new EventListener(gameManager), this);

        // Загрузка конфигов
        configManager.loadConfig();

        getLogger().info("TowerDefense enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("TowerDefense disabled!");
    }
}
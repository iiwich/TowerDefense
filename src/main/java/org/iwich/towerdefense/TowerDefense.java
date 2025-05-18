package org.iwich.towerdefense;

import org.bukkit.plugin.java.JavaPlugin;
import org.iwich.towerdefense.listener.EventListener;
import org.iwich.towerdefense.manager.CommandManager;
import org.iwich.towerdefense.manager.ConfigManager;
import org.iwich.towerdefense.manager.GameManager;

public class TowerDefense extends JavaPlugin {

    @Override
    public void onEnable() {
        // Инициализация менеджеров
        ConfigManager configManager = new ConfigManager(this);
        GameManager gameManager = new GameManager(this, configManager);

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
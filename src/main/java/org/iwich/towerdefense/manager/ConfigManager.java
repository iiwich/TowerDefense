package org.iwich.towerdefense.manager;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.iwich.towerdefense.data.MobData;
import org.iwich.towerdefense.data.TowerData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {
    private final JavaPlugin plugin;
    private FileConfiguration config;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    public Map<String, MobData> getMobTypes() {
        Map<String, MobData> mobTypes = new HashMap<>();
        ConfigurationSection mobsSection = config.getConfigurationSection("mobs");

        if (mobsSection == null) {
            mobsSection = config.createSection("mobs");

            addDefaultMob(mobsSection, "zombie", "Zombie", EntityType.ZOMBIE, 5, 0.15f, 5);
            addDefaultMob(mobsSection, "skeleton", "Skeleton", EntityType.SKELETON, 10, 0.25f, 10);
            addDefaultMob(mobsSection, "chicken", "Chicken", EntityType.CHICKEN, 2, 0.35f, 3);

            // Сохраняем конфиг
            plugin.saveConfig();
        }

        // Загружаем мобов из конфига
        for (String mobKey : mobsSection.getKeys(false)) {
            ConfigurationSection mobSection = mobsSection.getConfigurationSection(mobKey);
            if (mobSection != null) {
                EntityType type = EntityType.valueOf(mobSection.getString("type").toUpperCase());
                mobTypes.put(mobKey, new MobData(
                        mobSection.getString("name", mobKey),
                        type,
                        mobSection.getInt("health", 10),
                        (float) mobSection.getDouble("speed", 0.2),
                        mobSection.getInt("reward", 5)
                ));
            }
        }

        return mobTypes;
    }

    private void addDefaultMob(ConfigurationSection section, String key, String name, EntityType type,
                               int health, float speed, int reward) {
        ConfigurationSection mobSection = section.createSection(key);
        mobSection.set("name", name);
        mobSection.set("type", type.name());
        mobSection.set("health", health);
        mobSection.set("speed", speed);
        mobSection.set("reward", reward);
    }

    public List<TowerData> getTowerTypes() {
        List<TowerData> towerTypes = new ArrayList<>();

        for (String key : config.getConfigurationSection("towers").getKeys(false)) {
            String path = "towers." + key;

            towerTypes.add(new TowerData(
                    config.getString(path + ".name"),
                    config.getInt(path + ".damage"),
                    config.getDouble(path + ".attack_speed"),
                    config.getInt(path + ".range"),
                    config.getInt(path + ".cost"),
                    Material.matchMaterial(config.getString(path + ".material")),
                    config.getString(path + ".effect"),
                    config.getInt(path + ".effect_duration"),
                    config.getBoolean(path + ".poison_spread", false),
                    config.getDouble(path + ".poison_spread_radius", 3.0),
                    config.getInt(path + ".poison_spread_duration", 60)
            ));
        }

        return towerTypes;
    }

    public List<Vector> getPathPoints() {
        List<Vector> path = new ArrayList<>();
        List<String> pathStrings = config.getStringList("path.points");

        for (String point : pathStrings) {
            String[] coords = point.split(",");
            path.add(new Vector(
                    Double.parseDouble(coords[0]),
                    Double.parseDouble(coords[1]),
                    Double.parseDouble(coords[2])
            ));
        }

        return path;
    }

    public Vector getStartPoint() {
        String[] coords = config.getString("path.start", "0,64,0").split(",");
        return new Vector(
                Double.parseDouble(coords[0]),
                Double.parseDouble(coords[1]),
                Double.parseDouble(coords[2])
        );
    }


    public int getStartMoney() {
        return config.getInt("game.start_money", 50);
    }

    public int getStartLives() {
        return config.getInt("game.start_lives", 10);
    }
}

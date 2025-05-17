package org.iwich.towerdefense;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.iwich.towerdefense.models.MobType;
import org.iwich.towerdefense.models.TowerType;

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

    public Map<String, MobType> getMobTypes() {
        Map<String, MobType> mobTypes = new HashMap<>();

        mobTypes.put("zombie", new MobType(
                "Zombie",
                EntityType.ZOMBIE,
                5,
                0.15f,  // Было 0.3f (уменьшили скорость в 2 раза)
                config.getInt("mobs.zombie.reward", 5)
        ));

        mobTypes.put("skeleton", new MobType(
                "Skeleton",
                EntityType.SKELETON,
                10,
                0.25f,  // Было 0.5f
                config.getInt("mobs.skeleton.reward", 10)
        ));

        mobTypes.put("chicken", new MobType(
                "Chicken",
                EntityType.CHICKEN,
                2,
                0.35f,  // Было 0.7f
                config.getInt("mobs.chicken.reward", 3)
        ));

        return mobTypes;
    }

    public List<TowerType> getTowerTypes() {
        List<TowerType> towerTypes = new ArrayList<>();

        for (String key : config.getConfigurationSection("towers").getKeys(false)) {
            String path = "towers." + key;

            towerTypes.add(new TowerType(
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

    public Vector getEndPoint() {
        String[] coords = config.getString("path.end", "10,64,10").split(",");
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

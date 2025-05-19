package org.iwich.towerdefense.manager;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.iwich.towerdefense.task.GameTickTask;
import org.iwich.towerdefense.task.MobSpawnTask;
import org.iwich.towerdefense.data.MobData;
import org.iwich.towerdefense.data.TowerData;
import org.iwich.towerdefense.model.*;

import java.util.*;

public class GameManager {
    @Getter
    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    @Getter
    private boolean gameActive = false;
    @Getter
    private final Map<UUID, PlayerData> players = new HashMap<>();
    @Getter
    private final List<Mob> activeMobs = new ArrayList<>();
    @Getter
    private final List<Tower> towers = new ArrayList<>();
    @Getter
    private List<TowerData> towerTypes;

    private BukkitTask mobSpawnTask;
    private BukkitTask gameTickTask;

    public GameManager(JavaPlugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    public void startGame(Player player) {
        if (gameActive) {
            player.sendMessage("Игра уже запущена!");
            return;
        }

        // Загрузка конфигов
        Map<String, MobData> mobTypes = configManager.getMobTypes();
        towerTypes = configManager.getTowerTypes();
        List<Vector> path = configManager.getPathPoints();
        Vector startPoint = configManager.getStartPoint();

        if (path == null || path.isEmpty()) {
            player.sendMessage("Ошибка: путь для мобов не настроен!");
            return;
        }

        // Загрузка игрока
        players.put(player.getUniqueId(), new PlayerData(
                configManager.getStartMoney(),
                configManager.getStartLives()
        ));

        gameActive = true;

        // Запуск
        mobSpawnTask = new MobSpawnTask(this, mobTypes, startPoint, path).runTaskTimer(plugin, 20, 20 * 5);
        gameTickTask = new GameTickTask(this).runTaskTimer(plugin, 0, 1);

        player.sendMessage("§aИгра началась! У вас §e" + getPlayerData(player.getUniqueId()).getLives() + " §aжизней.");
    }

    public void stopGame(Player player) {
        if (!gameActive) {
            player.sendMessage("Игра не активна!");
            return;
        }

        // Остановка
        if (mobSpawnTask != null) mobSpawnTask.cancel();
        if (gameTickTask != null) gameTickTask.cancel();

        // Очистка мобов
        for (Mob mob : activeMobs) {
            if (mob.getEntity() != null) {
                mob.getEntity().remove();
            }
        }
        activeMobs.clear();

        // Очистка башен
        towers.clear();

        gameActive = false;
        player.sendMessage("§cИгра окончена!");
    }

    public PlayerData getPlayerData(UUID uuid) {
        return players.get(uuid);
    }

    public boolean placeTower(Player player, TowerData towerType, Location location) {
        // Проверка данных игрока
        PlayerData playerData = players.get(player.getUniqueId());
        if (playerData == null) {
            player.sendMessage("§cОшибка данных игрока!");
            return false;
        }

        // Проверка денег
        if (playerData.getMoney() < towerType.getCost()) {
            player.sendMessage("§cНедостаточно денег! Нужно: §e" + towerType.getCost());
            return false;
        }

        // Проверка местоположения
        if (location == null || location.getWorld() == null) {
            player.sendMessage("§cНельзя построить здесь!");
            return false;
        }

        if (!canPlaceTowerHere(location)) {
            player.sendMessage("§cЗдесь нельзя построить башню!");
            return false;
        }

        // Создаем башню
        Tower tower = new Tower(towerType, location, plugin);

        // Вычитаем деньги
        playerData.setMoney(playerData.getMoney() - towerType.getCost());
        towers.add(tower);

        player.sendMessage("§aБашня §e" + towerType.getName() + " §aпостроена!");
        return true;
    }

    public void mobReachedEnd(Mob mob) {
        PlayerData playerData = players.get(mob.getTargetPlayer());
        if (playerData == null) return;

        playerData.setLives(playerData.getLives() - 1);
        if (playerData.getLives() <= 0) {
            stopGame(Bukkit.getPlayer(mob.getTargetPlayer()));
        } else {
            Player player = Bukkit.getPlayer(mob.getTargetPlayer());
            if (player != null) {
                player.sendMessage("§cМоб достиг конца! Осталось жизней: §e" + playerData.getLives());
            }
        }
    }

    public void mobKilled(Mob mob) {
        PlayerData playerData = players.get(mob.getTargetPlayer());
        if (playerData == null) return;

        playerData.setMoney(playerData.getMoney() + mob.getMobType().getReward());
        activeMobs.remove(mob);
    }

    public void addMob(Mob mob) {
        if (mob != null && mob.getEntity() != null) {
            activeMobs.add(mob);
        }
    }

    private boolean canPlaceTowerHere(Location location) {
        // Проверяем, что блок подходит для строительства
        return location.getBlock().getType() == Material.AIR;
    }
}
package org.iwich.towerdefense;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import java.util.*;

public class GameManager {
    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private final PoisonEffect poisonEffect;

    private boolean gameActive = false;
    private Map<UUID, PlayerData> players = new HashMap<>();
    private List<Mob> activeMobs = new ArrayList<>();
    private List<Tower> towers = new ArrayList<>();
    private List<Vector> path;
    private Vector startPoint;
    private Vector endPoint;
    private Map<String, MobType> mobTypes;
    private List<TowerType> towerTypes;

    private BukkitTask mobSpawnTask;
    private BukkitTask gameTickTask;
    private int wave = 0;

    public GameManager(JavaPlugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.poisonEffect = new PoisonEffect(plugin);
    }

    public void startGame(Player player) {
        if (gameActive) {
            player.sendMessage("Игра уже запущена!");
            return;
        }

        // Загрузка конфигов
        mobTypes = configManager.getMobTypes();
        towerTypes = configManager.getTowerTypes();
        path = configManager.getPathPoints();
        startPoint = configManager.getStartPoint();
        endPoint = configManager.getEndPoint();

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
        wave = 0;

        // Запуск
        mobSpawnTask = new MobSpawnTask().runTaskTimer(plugin, 20, 20 * 5); // Начало через 1 сек, затем каждые 5 сек
        gameTickTask = new GameTickTask().runTaskTimer(plugin, 0, 1);

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

    public boolean isGameActive() {
        return gameActive;
    }

    public PlayerData getPlayerData(UUID uuid) {
        return players.get(uuid);
    }

    public List<TowerType> getTowerTypes() {
        return towerTypes;
    }

    public boolean placeTower(Player player, TowerType towerType, Location location) {
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

        try {
            // Создаем башню
            Tower tower = new Tower(towerType, location, plugin);

            // Вычитаем деньги
            playerData.setMoney(playerData.getMoney() - towerType.getCost());
            towers.add(tower);

            player.sendMessage("§aБашня §e" + towerType.getName() + " §aпостроена!");
            return true;
        } catch (Exception e) {
            player.sendMessage("§cОшибка при строительстве башни!");
            e.printStackTrace();
            return false;
        }
    }

    private boolean canPlaceTowerHere(Location location) {
        // Проверяем, что блок подходит для строительства
        if (location.getBlock().getType() != Material.AIR) {
            return false;
        }

        // Проверяем, что башня не мешает пути мобов
        for (Vector pathPoint : path) {
            if (pathPoint.toLocation(location.getWorld()).distanceSquared(location) < 4) {
                return false;
            }
        }

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

    private class MobSpawnTask extends BukkitRunnable {
        @Override
        public void run() {
            if (!gameActive) {
                this.cancel();
                return;
            }

            wave++;
            for (UUID uuid : players.keySet()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null || !player.isOnline()) continue;

                Location spawnLoc = startPoint.toLocation(player.getWorld());
                if (spawnLoc == null || spawnLoc.getWorld() == null) continue;

                Mob mob = null;
                if (wave % 5 == 0) {
                    // Спавн мобов на волнах
                    addMob(new Mob(mobTypes.get("skeleton"), player.getUniqueId(), spawnLoc, path));
                    addMob(new Mob(mobTypes.get("zombie"), player.getUniqueId(), spawnLoc, path));
                    addMob(new Mob(mobTypes.get("chicken"), player.getUniqueId(), spawnLoc, path));
                } else if (wave % 3 == 0) {
                    mob = new Mob(mobTypes.get("skeleton"), player.getUniqueId(), spawnLoc, path);
                } else if (wave % 2 == 0) {
                    mob = new Mob(mobTypes.get("zombie"), player.getUniqueId(), spawnLoc, path);
                } else {
                    mob = new Mob(mobTypes.get("chicken"), player.getUniqueId(), spawnLoc, path);
                }

                if (mob != null) {
                    addMob(mob);
                }
            }

            // Уведомление о волне
            for (UUID uuid : players.keySet()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null && player.isOnline()) {
                    player.sendMessage("§bВолна §e" + wave + "§b началась!");
                }
            }
        }
    }

    private class GameTickTask extends BukkitRunnable {
        @Override
        public void run() {
            if (!gameActive) {
                this.cancel();
                return;
            }

            // Создаем копию списка для безопасного перебора
            List<Mob> mobsToProcess = new ArrayList<>(activeMobs);
            List<Mob> deadMobs = new ArrayList<>();
            List<Mob> reachedEndMobs = new ArrayList<>();

            // Обработка мобов
            for (Mob mob : mobsToProcess) {
                try {
                    mob.update();

                    if (!mob.isAlive()) {
                        deadMobs.add(mob);
                    } else if (mob.hasReachedEnd()) {
                        reachedEndMobs.add(mob);
                    }
                } catch (Exception e) {
                    plugin.getLogger().warning("Ошибка при обновлении моба: " + e.getMessage());
                    deadMobs.add(mob); // Удаляем проблемного моба
                }
            }

            // Удаляем мертвых мобов
            for (Mob mob : deadMobs) {
                mobKilled(mob);
                activeMobs.remove(mob);
                if (mob.getEntity() != null) {
                    mob.getEntity().remove();
                }
            }

            // Обработка мобов, дошедших до конца
            for (Mob mob : reachedEndMobs) {
                mobReachedEnd(mob);
                activeMobs.remove(mob);
                if (mob.getEntity() != null) {
                    mob.getEntity().remove();
                }
            }

            // Обновление башен
            for (Tower tower : towers) {
                try {
                    tower.update(activeMobs); // Передаем актуальный список мобов
                } catch (Exception e) {
                    plugin.getLogger().warning("Ошибка при обновлении башни: " + e.getMessage());
                }
            }
        }
    }
}
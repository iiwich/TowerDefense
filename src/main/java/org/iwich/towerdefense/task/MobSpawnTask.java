package org.iwich.towerdefense.task;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.iwich.towerdefense.data.MobData;
import org.iwich.towerdefense.manager.GameManager;
import org.iwich.towerdefense.model.Mob;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MobSpawnTask extends BukkitRunnable {
    private final GameManager gameManager;
    private final Map<String, MobData> mobTypes;
    private final Vector startPoint;
    private final List<Vector> path;
    @Getter
    private int wave = 0;

    public MobSpawnTask(GameManager gameManager, Map<String, MobData> mobTypes, Vector startPoint, List<Vector> path) {
        this.gameManager = gameManager;
        this.mobTypes = mobTypes;
        this.startPoint = startPoint;
        this.path = path;
    }

    @Override
    public void run() {
        if (!gameManager.isGameActive()) {
            this.cancel();
            return;
        }

        wave++;
        for (UUID uuid : gameManager.getPlayers().keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline()) continue;

            Location spawnLoc = startPoint.toLocation(player.getWorld());
            if (spawnLoc == null || spawnLoc.getWorld() == null) continue;

            Mob mob = null;
            if (wave % 5 == 0) {
                // Спавн мобов на волнах
                gameManager.addMob(new Mob(mobTypes.get("skeleton"), player.getUniqueId(), spawnLoc, path));
                gameManager.addMob(new Mob(mobTypes.get("zombie"), player.getUniqueId(), spawnLoc, path));
                gameManager.addMob(new Mob(mobTypes.get("chicken"), player.getUniqueId(), spawnLoc, path));
            } else if (wave % 4 == 0) {
                mob = new Mob(mobTypes.get("evoker"), player.getUniqueId(), spawnLoc, path);
            } else if (wave % 3 == 0) {
                mob = new Mob(mobTypes.get("skeleton"), player.getUniqueId(), spawnLoc, path);
            } else if (wave % 2 == 0) {
                mob = new Mob(mobTypes.get("zombie"), player.getUniqueId(), spawnLoc, path);
            } else {
                mob = new Mob(mobTypes.get("chicken"), player.getUniqueId(), spawnLoc, path);
            }

            if (mob != null) {
                gameManager.addMob(mob);
            }
            player.sendMessage("§bВолна §e" + wave + "§b началась!");
        }
    }
}
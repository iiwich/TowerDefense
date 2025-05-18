package org.iwich.towerdefense.task;

import org.bukkit.scheduler.BukkitRunnable;
import org.iwich.towerdefense.manager.GameManager;
import org.iwich.towerdefense.model.Mob;
import org.iwich.towerdefense.model.Tower;

import java.util.ArrayList;
import java.util.List;

public class GameTickTask extends BukkitRunnable {
    private final GameManager gameManager;

    public GameTickTask(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void run() {
        if (!gameManager.isGameActive()) {
            this.cancel();
            return;
        }

        // Создаем копию списка для безопасного перебора
        List<Mob> mobsToProcess = new ArrayList<>(gameManager.getActiveMobs());
        List<Mob> deadMobs = new ArrayList<>();
        List<Mob> reachedEndMobs = new ArrayList<>();

        // Обработка мобов
        for (Mob mob : mobsToProcess) {
            mob.update();

            if (!mob.isAlive()) {
                deadMobs.add(mob);
            } else if (mob.hasReachedEnd()) {
                reachedEndMobs.add(mob);
            }
        }

        // Удаляем мертвых мобов
        for (Mob mob : deadMobs) {
            gameManager.mobKilled(mob);
            gameManager.getActiveMobs().remove(mob);
            if (mob.getEntity() != null) {
                mob.getEntity().remove();
            }
        }

        // Обработка мобов, дошедших до конца
        for (Mob mob : reachedEndMobs) {
            gameManager.mobReachedEnd(mob);
            gameManager.getActiveMobs().remove(mob);
            if (mob.getEntity() != null) {
                mob.getEntity().remove();
            }
        }

        // Обновление башен
        for (Tower tower : gameManager.getTowers()) {
            tower.update(gameManager.getActiveMobs()); // Передаем актуальный список мобов
        }
    }
}
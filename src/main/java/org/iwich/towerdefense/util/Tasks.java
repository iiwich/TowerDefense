package org.iwich.towerdefense.util;

import lombok.experimental.UtilityClass;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.iwich.towerdefense.TowerDefense;

import java.util.function.Consumer;

@UtilityClass
public class Tasks {

    public BukkitTask after(int delay, boolean async, Consumer<BukkitRunnable> handler) {
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                handler.accept(this);
            }
        };
        return async ?
                runnable.runTaskLaterAsynchronously(TowerDefense.get(), delay)
                :
                runnable.runTaskLater(TowerDefense.get(), delay);
    }

    public BukkitTask after(int delay, Consumer<BukkitRunnable> handler) {
        return after(delay, false, handler);
    }

    public BukkitTask after(Consumer<BukkitRunnable> handler) {
        return after(1, handler);
    }

    public BukkitTask afterAsync(int delay, Consumer<BukkitRunnable> handler) {
        return after(delay, true, handler);
    }

    public BukkitTask afterAsync(Consumer<BukkitRunnable> handler) {
        return afterAsync(1, handler);
    }

    public BukkitTask every(int delay, int period, boolean async, Consumer<BukkitRunnable> handler) {
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                handler.accept(this);
            }
        };
        return async ?
                runnable.runTaskTimerAsynchronously(TowerDefense.get(), delay, period)
                :
                runnable.runTaskTimer(TowerDefense.get(), delay, period);
    }

    public BukkitTask every(int delay, int period, Consumer<BukkitRunnable> handler) {
        return every(delay, period, false, handler);
    }

    public void every(int delayAndPeriod, Consumer<BukkitRunnable> handler) {
        every(delayAndPeriod, delayAndPeriod, false, handler);
    }

    public BukkitTask everyAsync(int delay, int period, Consumer<BukkitRunnable> handler) {
        return every(delay, period, true, handler);
    }

    public BukkitTask everyAsync(int delayAndPeriod, Consumer<BukkitRunnable> handler) {
        return everyAsync(delayAndPeriod, delayAndPeriod, handler);
    }
}

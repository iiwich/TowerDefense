package org.iwich.towerdefense;

import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class PoisonEffect {
    private final JavaPlugin plugin;

    public PoisonEffect(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void applyPoison(LivingEntity target, int duration, int amplifier) {
        target.addPotionEffect(new PotionEffect(
                PotionEffectType.POISON,
                duration,
                amplifier,
                false,
                true
        ));
    }

    public void spreadPoison(LivingEntity source, double radius, int duration, int amplifier) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (LivingEntity entity : source.getWorld().getLivingEntities()) {
                    if (entity.equals(source)) continue;

                    if (entity.getLocation().distance(source.getLocation()) <= radius) {
                        applyPoison(entity, duration, amplifier);

                        // Визуальный эффект распространения
                        entity.getWorld().spawnParticle(
                                Particle.VILLAGER_ANGRY,
                                entity.getLocation().add(0, 1, 0),
                                5,
                                0.3,
                                0.3,
                                0.3,
                                0.1
                        );
                    }
                }
            }
        }.runTask(plugin);
    }
}
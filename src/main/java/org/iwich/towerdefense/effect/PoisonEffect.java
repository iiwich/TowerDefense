package org.iwich.towerdefense.effect;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class PoisonEffect {

    public static void applyPoison(LivingEntity target, int duration, int amplifier) {
        target.addPotionEffect(new PotionEffect(
                PotionEffectType.POISON,
                duration,
                amplifier,
                false,
                true
        ));
    }

    public static void spreadPoison(Location centerLocation, double radius, int duration, int amplifier) {
        for (LivingEntity entity : centerLocation.getWorld().getLivingEntities()) {
            if (entity.getLocation().distance(centerLocation) <= radius) {
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
}
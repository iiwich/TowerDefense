package org.iwich.towerdefense.model;

import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.iwich.towerdefense.data.TowerData;
import org.iwich.towerdefense.effect.PoisonEffect;

import java.util.List;
import java.util.Objects;

public class Tower {
    @Getter
    private final TowerData towerType;
    @Getter
    private final Location location;
    private long lastAttackTime;

    public Tower(TowerData towerType, Location location, JavaPlugin plugin) {
        this.towerType = towerType;
        this.location = location;
        this.lastAttackTime = 0;

        // Установка блока
        if (location != null && location.getWorld() != null) {
            Block block = location.getBlock();
            if (block != null) {
                block.setType(towerType.getMaterial());
            }
        }
    }

    public void update(List<Mob> mobs) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastAttackTime < 1000 / towerType.getAttackSpeed()) return;

        Mob target = findTarget(mobs);
        if (target != null && target.isAlive()) {
            attack(target);
            lastAttackTime = currentTime;
        }
    }

    private Mob findTarget(List<Mob> mobs) {
        Mob closestTarget = null;
        double closestDistance = Double.MAX_VALUE;

        for (Mob mob : mobs) {
            if (!mob.isAlive()) continue;

            if (Objects.equals(getTowerType().getEffect(), "POISON")) {
                PoisonEffect.spreadPoison(location, towerType.getPoisonSpreadRadius(), towerType.getPoisonSpreadDuration(), 1);
            }

            double distance = mob.getEntity().getLocation().distance(location);
            if (distance <= towerType.getRange() && distance < closestDistance) {
                closestDistance = distance;
                closestTarget = mob;
            }
        }
        return closestTarget;
    }

    private void attack(Mob mob) {
        LivingEntity entity = mob.getEntity();

        // Базовый урон
        mob.damage(towerType.getDamage());

        // Визуальные эффекты
        spawnAttackParticles(entity.getLocation());
        playAttackSound();
    }


    private void spawnAttackParticles(Location target) {
        Location start = location.clone().add(0.5, 0.5, 0.5);
        Vector direction = target.toVector().subtract(start.toVector());

        for (double i = 0; i <= 1; i += 0.1) {
            Location particleLoc = start.clone().add(direction.clone().multiply(i));
            location.getWorld().spawnParticle(
                    Particle.FIREWORKS_SPARK,
                    particleLoc,
                    1
            );
        }
    }

    private void playAttackSound() {
        Sound sound = "POISON".equalsIgnoreCase(towerType.getEffect())
                ? Sound.ENTITY_SPIDER_AMBIENT
                : Sound.ENTITY_ARROW_SHOOT;

        location.getWorld().playSound(
                location,
                sound,
                0.7f,
                "POISON".equalsIgnoreCase(towerType.getEffect()) ? 0.8f : 1.2f
        );
    }
}
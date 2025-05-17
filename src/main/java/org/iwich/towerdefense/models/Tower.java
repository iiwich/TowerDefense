package org.iwich.towerdefense.models;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.iwich.towerdefense.data.TowerData;

import java.util.List;

public class Tower {
    private final TowerData towerType;
    private final Location location;
    private long lastAttackTime;
    private final JavaPlugin plugin;

    public Tower(TowerData towerType, Location location, JavaPlugin plugin) {
        this.towerType = towerType;
        this.location = location;
        this.plugin = plugin;
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

        // Применение яда (сломано)
        if ("POISON".equalsIgnoreCase(towerType.getEffect())) {
            applyPoisonEffect(entity);
        }

        // Визуальные эффекты
        spawnAttackParticles(entity.getLocation());
        playAttackSound();
    }

    private void applyPoisonEffect(LivingEntity target) {
        // Основной эффект яда
        target.addPotionEffect(new PotionEffect(
                PotionEffectType.POISON,
                towerType.getEffectDuration(),
                1
        ));

        // Распространение яда (не работает)
        if (towerType.hasPoisonSpread()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (LivingEntity entity : target.getWorld().getLivingEntities()) {
                        if (entity.equals(target)) continue;

                        if (entity.getLocation().distance(target.getLocation()) <= towerType.getPoisonSpreadRadius()) {
                            entity.addPotionEffect(new PotionEffect(
                                    PotionEffectType.POISON,
                                    towerType.getPoisonSpreadDuration(),
                                    0
                            ));

                            // Эффект частиц
                            target.getWorld().spawnParticle(
                                    Particle.VILLAGER_ANGRY,
                                    entity.getLocation().add(0, 1, 0),
                                    5,
                                    0.3, 0.3, 0.3,
                                    0.1
                            );
                        }
                    }
                }
            }.runTask(plugin);
        }
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

    public void remove() {
        location.getBlock().setType(Material.AIR);
    }

    public TowerData getTowerType() { return towerType; }
    public Location getLocation() { return location; }
}
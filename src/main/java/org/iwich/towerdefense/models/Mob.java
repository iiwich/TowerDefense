package org.iwich.towerdefense.models;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import org.iwich.towerdefense.data.MobData;

import java.util.List;
import java.util.UUID;

public class Mob {
    private final MobData mobType;
    private final UUID targetPlayer;
    private LivingEntity entity;
    private int currentHealth;
    private List<Vector> path;
    private int currentPathIndex = 0;

    public Mob(MobData mobType, UUID targetPlayer, Location spawnLocation, List<Vector> path) {
        this.mobType = mobType;
        this.targetPlayer = targetPlayer;
        this.path = path;
        this.currentHealth = mobType.getHealth();

        // Спавн конкретного типа моба
        EntityType entityType;
        switch (mobType.getName().toLowerCase()) {
            case "zombie":
                entityType = EntityType.ZOMBIE;
                break;
            case "skeleton":
                entityType = EntityType.SKELETON;
                break;
            case "chicken":
                entityType = EntityType.CHICKEN;
                break;
            default:
                entityType = EntityType.ZOMBIE; // По умолчанию зомби
        }

        this.entity = (LivingEntity) spawnLocation.getWorld().spawnEntity(spawnLocation, entityType);
        entity.setMaxHealth(mobType.getHealth());
        entity.setHealth(mobType.getHealth());
        entity.setCustomName(mobType.getName() + " [" + currentHealth + "/" + mobType.getHealth() + "]");
        entity.setCustomNameVisible(true);
    }

    public void update() {
        if (currentPathIndex >= path.size()) return;

        Vector target = path.get(currentPathIndex);
        Location targetLoc = target.toLocation(entity.getWorld());

        if (entity == null || entity.isDead() || targetLoc == null) {
            return;
        }

        // Получаем скорость из MobType с проверкой
        float speed = Math.max(0.05f, mobType.getSpeed()); // Минимальная скорость 0.05 (если меньше то там пиздец)

        Vector direction = targetLoc.toVector().subtract(entity.getLocation().toVector());

        if (direction.lengthSquared() > 0) {
            direction.normalize().multiply(speed); // Используем скорость из кфг

            if (Double.isFinite(direction.getX()) &&
                    Double.isFinite(direction.getY()) &&
                    Double.isFinite(direction.getZ())) {
                entity.setVelocity(direction);
            }
        }

        // Проверяем достижение точки
        if (entity.getLocation().distanceSquared(targetLoc) < 0.25) {
            currentPathIndex++;
        }
    }

    public boolean hasReachedEnd() {
        return currentPathIndex >= path.size();
    }

    public void damage(int amount) {
        currentHealth -= amount;
        entity.setCustomName(mobType.getName() + " [" + currentHealth + "/" + mobType.getHealth() + "]");

        if (currentHealth <= 0) {
            entity.remove();
        }
    }

    public MobData getMobType() { return mobType; }
    public UUID getTargetPlayer() { return targetPlayer; }
    public LivingEntity getEntity() { return entity; }
    public boolean isAlive() { return currentHealth > 0; }

    public Vector getLocation() {
        return null;
    }
}
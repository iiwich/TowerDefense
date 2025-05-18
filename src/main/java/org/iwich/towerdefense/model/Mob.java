package org.iwich.towerdefense.model;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import org.iwich.towerdefense.data.MobData;

import java.util.List;
import java.util.UUID;

public class Mob {
    @Getter
    private final MobData mobType;
    @Getter
    private final UUID targetPlayer;
    @Getter
    private LivingEntity entity;
    private int currentHealth;
    private final List<Vector> path;
    private int currentPathIndex = 0;

    public Mob(MobData mobType, UUID targetPlayer, Location spawnLocation, List<Vector> path) {
        this.mobType = mobType;
        this.targetPlayer = targetPlayer;
        this.path = path;
        this.currentHealth = mobType.getHealth();

        this.entity = (LivingEntity) spawnLocation.getWorld().spawnEntity(spawnLocation, mobType.getEntityType());
        if (entity != null) {
            Attributable at = entity;
            at.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(2000.0);
        }
        assert entity != null;
        entity.setHealth(mobType.getHealth());
        entity.setCustomName("§c" + mobType.getName() + "§a [" + currentHealth + "/" + mobType.getHealth() + "]");
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
        float speed = Math.max(0.03f, mobType.getSpeed()); // Минимальная скорость 0.03

        Vector direction = targetLoc.toVector().subtract(entity.getLocation().toVector());

        if (direction.lengthSquared() > 0) {
            direction.normalize().multiply(speed); // Используем скорость из кфг
                entity.setVelocity(direction);
        }

        // Проверяем достижение точки
        if (entity.getLocation().distanceSquared(targetLoc) < 0.05) {
            currentPathIndex++;
        }
    }

    public boolean hasReachedEnd() {
        return currentPathIndex >= path.size();
    }

    public void damage(int amount) {
        currentHealth -= amount;
        entity.setCustomName("§c" + mobType.getName() + "§a [" + currentHealth + "/" + mobType.getHealth() + "]");

        if (currentHealth <= 0) {
            entity.remove();
        }
    }

    public boolean isAlive() { return currentHealth > 0; }
}
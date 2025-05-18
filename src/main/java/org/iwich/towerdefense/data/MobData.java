package org.iwich.towerdefense.data;

import lombok.Getter;
import org.bukkit.entity.EntityType;

@Getter
public class MobData {
    private final EntityType entityType;
    private final String name;
    private final int health;
    private final float speed;
    private final int reward;

    public MobData(String name, EntityType entityType, int health, float speed, int reward) {
        this.name = name;
        this.entityType = entityType;
        this.health = health;
        this.speed = speed;
        this.reward = reward;
    }
}
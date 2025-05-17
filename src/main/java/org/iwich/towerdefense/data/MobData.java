package org.iwich.towerdefense.data;

import org.bukkit.entity.EntityType;

public class MobData {
    private final String name;
    private final EntityType entityType;
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

    // Геттеры
    public String getName() { return name; }
    public int getHealth() { return health; }
    public float getSpeed() { return speed; }
    public int getReward() { return reward; }
}
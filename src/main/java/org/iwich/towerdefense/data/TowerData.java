package org.iwich.towerdefense.models;

import org.bukkit.Material;

public class TowerData {
    private final String name;
    private final int damage;
    private final double attackSpeed;
    private final int range;
    private final int cost;
    private final Material material;
    private final String effect;
    private final int effectDuration;
    private final boolean poisonSpread;
    private final double poisonSpreadRadius;
    private final int poisonSpreadDuration;

    public TowerData(String name, int damage, double attackSpeed, int range,
                     int cost, Material material, String effect, int effectDuration,
                     boolean poisonSpread, double poisonSpreadRadius, int poisonSpreadDuration) {
        this.name = name;
        this.damage = damage;
        this.attackSpeed = attackSpeed;
        this.range = range;
        this.cost = cost;
        this.material = material;
        this.effect = effect;
        this.effectDuration = effectDuration;
        this.poisonSpread = poisonSpread;
        this.poisonSpreadRadius = poisonSpreadRadius;
        this.poisonSpreadDuration = poisonSpreadDuration;
    }

    // Геттеры
    public String getName() { return name; }
    public int getDamage() { return damage; }
    public double getAttackSpeed() { return attackSpeed; }
    public int getRange() { return range; }
    public int getCost() { return cost; }
    public Material getMaterial() { return material; }
    public String getEffect() { return effect; }
    public int getEffectDuration() { return effectDuration; }
    public boolean hasPoisonSpread() { return poisonSpread; }
    public double getPoisonSpreadRadius() { return poisonSpreadRadius; }
    public int getPoisonSpreadDuration() { return poisonSpreadDuration; }
}
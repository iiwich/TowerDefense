package org.iwich.towerdefense.data;

import lombok.Getter;
import org.bukkit.Material;


public class TowerData {
    @Getter
    private final String name;
    @Getter
    private final int damage;
    @Getter
    private final double attackSpeed;
    @Getter
    private final int range;
    @Getter
    private final int cost;
    @Getter
    private final Material material;
    @Getter
    private final String effect;
    @Getter
    private final int effectDuration;
    private final boolean poisonSpread;
    @Getter
    private final double poisonSpreadRadius;
    @Getter
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
    public boolean hasPoisonSpread() { return poisonSpread; }
}
package org.iwich.towerdefense.model;

import lombok.Getter;
import lombok.Setter;
import org.iwich.towerdefense.data.TowerData;

@Setter
@Getter
public class PlayerData {
    private int money;
    private int lives;
    private TowerData selectedTower;

    public PlayerData(int money, int lives) {
        this.money = money;
        this.lives = lives;
    }

}
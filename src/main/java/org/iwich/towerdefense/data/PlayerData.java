package org.iwich.towerdefense.models;

public class PlayerData {
    private int money;
    private int lives;
    private TowerData selectedTower;

    public PlayerData(int money, int lives) {
        this.money = money;
        this.lives = lives;
    }

    // Геттеры и сеттеры
    public int getMoney() { return money; }
    public void setMoney(int money) { this.money = money; }
    public int getLives() { return lives; }
    public void setLives(int lives) { this.lives = lives; }
    public TowerData getSelectedTower() { return selectedTower; }
    public void setSelectedTower(TowerData towerType) { this.selectedTower = towerType; }
}
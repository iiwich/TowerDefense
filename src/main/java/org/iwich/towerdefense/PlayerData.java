package org.iwich.towerdefense;

public class PlayerData {
    private int money;
    private int lives;
    private TowerType selectedTower;

    public PlayerData(int money, int lives) {
        this.money = money;
        this.lives = lives;
    }

    // Геттеры и сеттеры
    public int getMoney() { return money; }
    public void setMoney(int money) { this.money = money; }
    public int getLives() { return lives; }
    public void setLives(int lives) { this.lives = lives; }
    public TowerType getSelectedTower() { return selectedTower; }
    public void setSelectedTower(TowerType towerType) { this.selectedTower = towerType; }
}
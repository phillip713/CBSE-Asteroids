package dk.sdu.cbse.score;

public class GameState {
    private int score = 0;
    private int health = 5; // Baseline starting health

    // Getters and Setters
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }
}
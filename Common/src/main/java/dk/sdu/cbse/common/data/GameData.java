package dk.sdu.cbse.common.data;

public class GameData {
    private double deltaTime;
    private int displayWidth = 800;
    private int displayHeight = 600;
    // Simple mock keyboard handling
    private final boolean[] keys = new boolean[4];
    public static final int UP = 0, LEFT = 1, RIGHT = 2, SPACE = 3;

    public double getDeltaTime() { return deltaTime; }
    public void setDeltaTime(double deltaTime) { this.deltaTime = deltaTime; }
    public int getDisplayWidth() { return displayWidth; }
    public int getDisplayHeight() { return displayHeight; }
    public void setKey(int key, boolean isPressed) { keys[key] = isPressed; }
    public boolean isKeyPressed(int key) { return keys[key]; }
}
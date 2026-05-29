package dk.sdu.cbse.asteroid;

import dk.sdu.cbse.common.data.Entity;

public class Asteroid extends Entity {
    private int size; // 3 = Large, 2 = Medium, 1 = Small

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
}
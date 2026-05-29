package dk.sdu.cbse.asteroid;

import dk.sdu.cbse.common.data.Entity;

public class Asteroid extends Entity {
    private int size; // 3 = Large, 2 = Medium, 1 = Small

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
        updateSizeAndShape(); // Automatically calculate properties based on the new size
    }

    private void updateSizeAndShape() {
        int radius = (this.size == 3) ? 25 : (this.size == 2) ? 15 : 7;
        this.setRadius(radius);

        // A bumpy 8-pointed asteroid layout scaled by its specific radius
        this.setShapeX(new double[]{
                radius, radius * 0.5, 0, -radius * 0.6, -radius, -radius * 0.4, 0, radius * 0.7
        });
        this.setShapeY(new double[]{
                0, radius * 0.7, radius, radius * 0.5, 0, -radius * 0.8, -radius, -radius * 0.5
        });
    }
}
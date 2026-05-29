package dk.sdu.cbse.common.data;

import java.io.Serializable;
import java.util.UUID;

public class Entity implements Serializable {
    private final UUID id = UUID.randomUUID();
    private double x, y;
    private double dx, dy;
    private double rotation;
    private double radius;
    private EntityType type;

    // Getters and setters
    public EntityType getType() { return type; }
    public void setType(EntityType type) { this.type = type; }
    public String getID() { return id.toString(); }
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    public double getDx() { return dx; }
    public void setDx(double dx) { this.dx = dx; }
    public double getDy() { return dy; }
    public void setDy(double dy) { this.dy = dy; }
    public double getRotation() { return rotation; }
    public void setRotation(double rotation) { this.rotation = rotation; }
    public double getRadius() { return radius; }
    public void setRadius(double radius) { this.radius = radius; }
}
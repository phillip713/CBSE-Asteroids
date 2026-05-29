package dk.sdu.cbse.bullet;

import dk.sdu.cbse.common.data.Entity;

public class Bullet extends Entity {
    private double expiration = 1.5; // Lifespan in seconds

    public double getExpiration() { return expiration; }
    public void setExpiration(double expiration) { this.expiration = expiration; }
}
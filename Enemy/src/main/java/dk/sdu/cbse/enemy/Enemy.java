package dk.sdu.cbse.enemy;

import dk.sdu.cbse.common.data.Entity;

public class Enemy extends Entity {
    private double fireCooldown = 0;

    public double getFireCooldown() { return fireCooldown; }
    public void setFireCooldown(double cooldown) { this.fireCooldown = cooldown; }
}
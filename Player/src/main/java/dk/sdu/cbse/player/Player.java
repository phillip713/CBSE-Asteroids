package dk.sdu.cbse.player;

import dk.sdu.cbse.common.data.Entity;

public class Player extends Entity {
    private double fireCooldown = 0;

    public double getFireCooldown() { return fireCooldown; }
    public void setFireCooldown(double cooldown) { this.fireCooldown = cooldown; }
}
package dk.sdu.cbse.asteroid;

import dk.sdu.cbse.common.data.EntityType;

public class AsteroidFactory {

    public static Asteroid createAsteroid(int size, double x, double y) {
        Asteroid asteroid = new Asteroid();
        asteroid.setType(EntityType.ASTEROID);
        asteroid.setSize(size);
        asteroid.setX(x);
        asteroid.setY(y);

        double randomAngle = Math.random() * Math.PI * 2;
        // Smaller pieces drift faster
        double speed = (size == 3) ? (40.0 + Math.random() * 30.0) : (60.0 + Math.random() * 40.0);

        asteroid.setDx(Math.cos(randomAngle) * speed);
        asteroid.setDy(Math.sin(randomAngle) * speed);
        asteroid.setRotation(Math.random() * Math.PI * 2);

        return asteroid;
    }
}
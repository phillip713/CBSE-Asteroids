package dk.sdu.cbse.player;

import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerControlSystemTest {

    private PlayerControlSystem playerControlSystem;
    private GameData gameData;
    private World world;
    private Player player;

    @BeforeEach
    public void setUp() {
        playerControlSystem = new PlayerControlSystem();
        gameData = new GameData();
        world = new World();

        player = new Player();
        player.setX(100);
        player.setY(100);
        player.setRotation(0);
        player.setDx(0);
        player.setDy(0);
        player.setIsDead(false);

        world.addEntity(player);
    }

    @Test
    public void testPlayerAcceleratesForwardWhenUpKeyPressed() {
        gameData.setKey(GameData.UP, true);
        gameData.setDeltaTime(1.0);

        playerControlSystem.process(gameData, world);

        // Assert
        // Math check: rotation 0 means accelerating purely on the X axis.
        // dx should become: 0 + cos(0) * 150 * 1 = 150.
        // position X should become: 100 + 150 * 1 = 250.
        assertTrue(player.getX() > 100, "Player should have moved forward on the X axis");
        assertEquals(100, player.getY(), "Player should not have drifted vertically at rotation 0");
        assertTrue(player.getDx() > 0, "Velocity vector DX should be positive");
    }
}
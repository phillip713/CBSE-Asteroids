package dk.sdu.cbse.score;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/state")
public class ScoreController {

    private final GameState gameState = new GameState();

    @GetMapping
    public GameState getGameState() {
        return gameState;
    }

    // 💡 Changed from query param to clean path variable: /api/state/score/10
    @PostMapping("/score/{points}")
    public GameState addScore(@PathVariable int points) {
        gameState.setScore(gameState.getScore() + points);
        return gameState;
    }

    @PostMapping("/damage")
    public GameState takeDamage() {
        gameState.setHealth(Math.max(0, gameState.getHealth() - 1));
        return gameState;
    }

    @PostMapping("/reset")
    public GameState resetGame() {
        gameState.setScore(0);
        gameState.setHealth(3);
        return gameState;
    }
}
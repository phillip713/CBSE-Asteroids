package dk.sdu.cbse.score;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ScoringSystem {

    private int totalScore = 0;

    public static void main(String[] args) {
        SpringApplication.run(ScoringSystem.class, args);
    }

    // GET http://localhost:8081/score
    @GetMapping("/score")
    public int addPoints(@RequestParam(value = "point") int point) {
        totalScore += point;
        return totalScore;
    }
}
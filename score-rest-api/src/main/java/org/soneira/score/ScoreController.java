package org.soneira.score;

import org.soneira.score.junit.ScoreResult;
import org.soneira.score.junit.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ScoreController {

    @Autowired
    private ScoreService scoreService;

    @RequestMapping("/scores")
    public List<ScoreResult> scores() {
        return scoreService.getAllScores();
    }

    @RequestMapping("/score/{team}/{points}")
    public ScoreResult teamPoints(@PathVariable String team, @PathVariable Integer points) {
        return scoreService.addScore(team, points);
    }
}

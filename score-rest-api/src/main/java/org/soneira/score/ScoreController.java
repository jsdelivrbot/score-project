package org.soneira.score;

import org.soneira.score.junit.ScoreResult;
import org.soneira.score.junit.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ScoreController {

    @Autowired
    private ScoreService scoreService;

    @RequestMapping("/scores")
    public List<ScoreResult> scores() {
        return scoreService.getAllScores();
    }

    @RequestMapping(value = "/score/{team}/{points}", method = RequestMethod.POST)
    public ScoreResult teamPoints(@PathVariable String team, @PathVariable Integer points) {
        return scoreService.addScore(team, points);
    }
}

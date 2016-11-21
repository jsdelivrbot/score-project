package org.soneira.score;

import org.soneira.score.junit.model.Score;
import org.soneira.score.junit.model.ScoreResult;
import org.soneira.score.junit.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    @RequestMapping("/rank")
    public List<Rank> rank() {
        return scoreService.getAllScores()
                .stream()
                .map(scoreResult ->
                        new Rank(scoreResult.getTeam(),
                                scoreResult.getScores().stream()
                                        .max(Comparator.comparing(Score::getIteration))
                                        .map(Score::getPoints).orElse(0)))
                .sorted(Comparator.comparing(Rank::getPoints).reversed())
                .collect(Collectors.toList());
    }
}

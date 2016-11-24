package com.dojocoders.score.api;

import com.dojocoders.score.model.ScoreResult;
import com.dojocoders.score.model.Score;
import com.dojocoders.score.service.ScoreService;
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
        return scoreService.getAllScoresFilled();
    }

    @RequestMapping("/scores/{lastItems}")
    public List<ScoreResult> scoresLastItems(@PathVariable Integer lastItems) {
        return scoreService.getAllScoresFilled()
                .stream()
                .map(scoreResult ->
                        new ScoreResult(scoreResult.getTeam(),
                                scoreResult.getScores()
                                        .stream()
                                        .sorted(Comparator.comparing(Score::getSprint).reversed())
                                        .limit(lastItems).sorted(Comparator.comparing(Score::getSprint)).collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/score/{team}/{points}", method = RequestMethod.POST)
    public ScoreResult addScoreTeamPoints(@PathVariable String team, @PathVariable Integer points) {
        return scoreService.addScore(team, points);
    }

}

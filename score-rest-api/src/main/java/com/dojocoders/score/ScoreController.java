package com.dojocoders.score;

import com.dojocoders.score.junit.model.ScoreResult;
import com.dojocoders.score.junit.model.Score;
import com.dojocoders.score.junit.ScoreService;
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

    @RequestMapping(value = "/score/{team}/{points}", method = RequestMethod.POST)
    public ScoreResult teamPoints(@PathVariable String team, @PathVariable Integer points) {
        return scoreService.addScore(team, points);
    }

}

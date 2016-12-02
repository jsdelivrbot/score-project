package com.dojocoders.score.api;

import com.dojocoders.score.model.ScoreResult;
import com.dojocoders.score.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scores")
@CrossOrigin
public class ScoreController {

    @Autowired
    private ScoreService scoreService;

    @RequestMapping
    public List<ScoreResult> scores() {
        return scoreService.getAllScoresFilled();
    }

    @RequestMapping(value = "/add/{team}/{points}", method = RequestMethod.POST)
    public ScoreResult addScoreTeamPoints(@PathVariable String team, @PathVariable Integer points) {
        return scoreService.addScore(team, points);
    }

}

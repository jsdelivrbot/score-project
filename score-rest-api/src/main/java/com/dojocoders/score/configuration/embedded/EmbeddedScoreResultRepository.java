package com.dojocoders.score.configuration.embedded;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.dojocoders.score.model.ScoreResult;
import com.dojocoders.score.repository.ScoreResultRepository;

@Repository
@Profile("embedded-persistence")
public class EmbeddedScoreResultRepository extends AbstractEmbeddedCrudRepository<ScoreResult, String> implements ScoreResultRepository {

	@Override
	protected String getId(ScoreResult entity) {
		return entity.getTeam();
	}
}

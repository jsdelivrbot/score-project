package org.soneira.score.junit;

import com.couchbase.client.deps.com.fasterxml.jackson.core.JsonProcessingException;
import com.couchbase.client.java.transcoder.JacksonTransformers;

import java.io.IOException;

public class ScoreResultJacksonTransformerMapper {


    public static ScoreResult fromJSON(String json) {
        try {
            return JacksonTransformers.MAPPER.readValue(json, ScoreResult.class);
        } catch (IOException e) {
           throw new RuntimeException(e);
        }
    }

    public static String toJSONString(ScoreResult score) throws JsonProcessingException {
        return JacksonTransformers.MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(score);
    }

}

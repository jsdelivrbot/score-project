package org.soneira.score.example;

import org.soneira.score.junit.ScoreBlockJUnit4ClassRunner;
import org.soneira.score.junit.annotations.InjectImpl;
import org.soneira.score.junit.annotations.Score;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(ScoreBlockJUnit4ClassRunner.class)
//@Persist(Couchbase.class)
public class BattleCode2016ValidationTest {


    @InjectImpl
    protected IBattleCode2016 battleCode2016;


    @Test
    @Score
    public void checkSum_For2and2_then4() {
        // Setup
        int expected = 4;

        // Test
        int result = battleCode2016.sum(2,2);

        // Assertions
        assertThat(result).isEqualTo(expected);
    }


    @Test
    @Score(50)
    public void checkSum_For2and2_then4_withMorePoints() {
        // Setup
        int expected = 4;

        // Test
        int result = battleCode2016.sum(2,2);

        // Assertions
        assertThat(result).isEqualTo(expected);
    }

    @Test(timeout = 1)
    @Score(value = 500, maxTimeOnly = true)
    public void checkSum_longTreatment_KO() throws InterruptedException {
        // Setup
        int expected = 9;

        for (int i = 0; i < 100000; i++) {
            // System.out.println("EO");
        }
        // Test
        int result = battleCode2016.sum(2,2);

        // Assertions
        assertThat(result).isEqualTo(expected);
    }

    @Test(timeout = 1000)
    @Score(value = 500, maxTimeOnly = true)
    public void checkSum_longTretment_OK_butWrongScore() throws InterruptedException {
        // Setup
        int expected = 9;

        // Test
        int result = battleCode2016.sum(2,2);

        // Assertions
        assertThat(result).isEqualTo(expected);
    }

    @Test(timeout = 1000)
    @Score(value = 500, maxTimeOnly = true)
    public void checkSum_longTretment_OK() throws InterruptedException {
        // Setup
        int expected = 4;

        // Test
        int result = battleCode2016.sum(2,2);

        // Assertions
        assertThat(result).isEqualTo(expected);
    }

}

# SCORE PROJECT #

Project for coding dojos with tests in junit

### Dependencies ###

* Maven 3.X ou +
* Java 8
* Npm
* Angular 2
* Typings 1.X

## SCORE JUNIT ##

Library to let launch test junit with a Score Mode

### Description ###

Let use de Runner ScoreBlockJUnit4ClassRunner for launch test with a Score mode, you can choose if the test only score when is ok or ko with a maxtime.
 
Persist your results on a database.

Exemple :

```
@RunWith(ScoreBlockJUnit4ClassRunner.class)
@Persist(Couchbase.class)
public class BattleCode2016Test {


    @InjectImpl
    protected IBattleCode2016 battleCode2016;


    @Test
    @Score(10)
    public void checkSum_For2and2_then4() {
        // Setup
        int expected = 4;

        // Test
        int result = battleCode2016.sum(2,2);

        // Assertions
        Assertions.assertThat(result).isEqualTo(expected);
    }

    @Test(timeout = 1)
    @Score(value = 500, maxTimeOnly = true)
    public void checkSum_longTretment() throws InterruptedException {
        // Setup
        int expected = 9;

        for (int i = 0; i < 100000; i++) {
            // System.out.println("EO");
        }
        // Test
        int result = battleCode2016.sum(2,2);

        // Assertions
        Assertions.assertThat(result).isEqualTo(expected);
    }
}
```

## SCORE REST API ##

Spring boot application for share resuts form SCORE JUNIT

## SCORE IHM ##

IHM on Angular to show the SCORE JUNIT results using the SCORE REST API

### Execute ###
npm install
npm start

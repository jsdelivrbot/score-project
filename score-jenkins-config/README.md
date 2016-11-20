# SCORE JENKINS CONFIGURATION #

Example of configuration to use score project with a Jenkins server

### Dependencies ###

* Java 8
* Jenkins 2.32 ou +
* Jenkins git, maven, pipeline & dsl plugin

## Installation ##

Start Jenkins via a docker container or by downloading jenkins war launched with the jenkins-start script example in this directory.
Install plugins.
Create teams as jenkins users.
Create a 'seed' project that clone your exercise repository (or this one if you want to use the example).
Add a DSL step configured with your jobs dsl (or exercise_example_jenkins_jobs.groovy of this repository).
Launch your 'seed' project in order to create all jobs.


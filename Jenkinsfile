pipeline {
	agent none
	options {
		timeout(time: 1, unit: 'HOURS')
		timestamps()
	}
	def projectVersion
	stages {
		stage('Front build & Test') {
			agent {
				label 'npm'
			}
			steps {
				dir('score-ihm') {
					sh '''
						rm -rf node_modules
						npm config set proxy http://soft:coucou@dvdsi110w.creteil.francetelecom.fr:8080
						npm install
						npm run tsc
					'''
				}
			}
		}
		stage('Back build & Test') {
			agent {
				label 'maven'
			}
			tools {
				jdk 'JDK'
				maven 'Maven'
			}
			steps {
				def retreiveVersionScript="mvn -Dexec.executable='echo' -Dexec.args='\${project.version}' --non-recursive --batch-mode exec:exec -q"
				projectVersion = sh(returnStdout: true, script: retreiveVersionScript).trim()
				sh 'mvn clean deploy -DperformRelease=true'
			}
			post {
				always {
					junit 'target/*.xml'
				}
			}
		}
		stage('Deploy') {
			when {
				branch 'master'
			}
			agent {
				label 'master'
			}
			tools {
				'org.jenkinsci.plugins.docker.commons.tools.DockerTool' 'Docker Compose'
			}
			steps {
				sh 'docker-compose down || true'

				dir('score-deploy') {
					stash includes: '**', name: 'deployment'
				}

				dir("${JENKINS_HOME}/DockerApps/codingwars-dashboard") {
					deleteDir()

					unstash 'deployment'

					sh "wget http://softcu-nexus.si.francetelecom.fr/nexus/service/local/artifact/maven/content?r=public&g=com.dojocoders&a=score-rest-api&v=${projectVersion} -O score-rest-api.jar"
					sh 'mv spring-boot-config score-rest-api && mv score-rest-api.jar score-rest-api'

					sh "wget http://softcu-nexus.si.francetelecom.fr/nexus/service/local/artifact/maven/content?r=public&g=com.dojocoders&a=score-ihm&v=${projectVersion}&e=zip -O score-ihm.zip"
					sh 'unzip score-ihm.zip'

					sh 'docker-compose up -d'
				}
			}
		}
	}
}

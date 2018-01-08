def projectVersion
def deployedBranches = ['master', 'deploy-optimizing']

pipeline {
	agent none
	options {
		timeout(time: 1, unit: 'HOURS')
		timestamps()
		skipDefaultCheckout() // No checkout on deploy stage custom workspace
	}
	stages {
		stage('Front build & Test') {
			agent {
				label 'npm'
			}
			steps {
				checkout scm
				dir('score-ihm') {
					sh '''
						rm -rf node_modules dist
						npm config set proxy http://soft:coucou@dvdsi110w.creteil.francetelecom.fr:8080
						npm install
						node_modules/@angular/cli/bin/ng build # --prod
					'''
				}
				stash includes: 'score-ihm/dist/**', name: 'front-app' // Zipped and uploaded to Nexus via Maven in back build
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
				checkout scm
				unstash 'front-app'
				configFileProvider([configFile(fileId: 'maven-deploy-settings', variable: 'MAVEN_SETTINGS')]) {
					sh 'mvn -s $MAVEN_SETTINGS clean deploy -DperformRelease=true'
				}

				// For deployment configuration
				if (deployedBranches.contains(env.BRANCH_NAME)) {
					script {
						def retreiveVersionScript = "mvn -Dexec.executable='echo' -Dexec.args='\${project.version}' --non-recursive --batch-mode exec:exec -q"
						projectVersion = sh(returnStdout: true, script: retreiveVersionScript).trim()
					}
					dir('score-deploy') {
						stash includes: '**', name: 'deployment'
					}
				}
			}
			post {
				always {
					junit allowEmptyResults: true, testResults: '**/target/surefire-reports/**/*.xml,**/target/failsafe-reports/**/*.xml'
				}
			}
		}
		if (deployedBranches.contains(env.BRANCH_NAME)) {
			stage('Deploy') {
				agent {
					node {
						label 'master'
						customWorkspace "${JENKINS_HOME}/DockerApps/codingwars-dashboard"
					}
				}
				tools {
					'org.jenkinsci.plugins.docker.commons.tools.DockerTool' 'Docker Compose'
				}
				steps {
					sh 'docker-compose down || true'
	
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

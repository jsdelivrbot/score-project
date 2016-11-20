SET GIT_HOME=D:\dev\PortableGit-2.10.2
SET JAVA_HOME=D:\dev\jdk1.8.0_112
SET M2_HOME=D:\dev\apache-maven-3.3.9

SET PATH=%GIT_HOME%\bin;%JAVA_HOME%\bin;%M2_HOME%\bin;%PATH%

SET JENKINS_HOME=D:\dev\Jenkins\home

java -jar jenkins-2.32.war

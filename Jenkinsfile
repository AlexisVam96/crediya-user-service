pipeline {
    agent any

    tools {
        jdk 'jdk17'       // el nombre configurado en Global Tool Configuration
        maven 'Maven'     // idem para Maven
    }

    stages {
        stage('Build Maven') {
            steps {
                bat 'mvn clean install -DskipTests'
            }
        }
    }
}
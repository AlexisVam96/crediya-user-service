pipeline {
    agent any

    tools {
        jdk 'jdk17'       // el nombre configurado en Global Tool Configuration
        gradle 'Gradle'    // idem para Maven
    }

    stages {
        stage('Build Maven') {
            steps {
                bat './gradlew clean build -x test'
            }
        }
    }
}
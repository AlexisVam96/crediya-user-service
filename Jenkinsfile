pipeline {
    agent any

    tools {
        jdk 'jdk17'       // el nombre configurado en Global Tool Configuration
    }

    environment {
        ACR_NAME = 'crediyauserregistry.azurecr.io'
        IMAGE_NAME = 'crediya-user-service'
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        RESOURCE_GROUP = 'crediya'
        AKS_CLUSTER = 'crediya-cluster-aks'
        DOCKERFILE_PATH = 'deployment/Dockerfile'
    }

    stages {
        stage('Build Gradle') {
            steps {
                bat './gradlew clean build -x test'
            }
        }

        stage('Build Docker Image ACR') {
            steps {
                echo '🐳 Building Docker image...'
                bat """
                docker build -t %ACR_NAME%/%IMAGE_NAME%:%IMAGE_TAG% -f %DOCKERFILE_PATH% .
                """
            }
        }

        stage('Login to ACR') {
            steps {
                echo '🔑 Logging into Azure Container Registry...'
                bat "az acr login --name %ACR_NAME%"
            }
        }
    }

}
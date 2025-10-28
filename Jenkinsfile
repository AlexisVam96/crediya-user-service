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

        stage('Login to Azure & Push Image') {
            steps {
                withCredentials([string(credentialsId: 'AZURE_CREDENTIALS', variable: 'AZURE_CRED_JSON')]) {
                    bat '''
                        echo %AZURE_CRED_JSON% > azure.json

                        for /f %%i in ('jq -r ".clientId" azure.json') do set CLIENT_ID=%%i
                        for /f %%i in ('jq -r ".clientSecret" azure.json') do set CLIENT_SECRET=%%i
                        for /f %%i in ('jq -r ".tenantId" azure.json') do set TENANT_ID=%%i

                        az login --service-principal --username %CLIENT_ID% --password %CLIENT_SECRET% --tenant %TENANT_ID%
                        az acr login --name crediyauserregistry
                        docker push %ACR_NAME%/%IMAGE_NAME%:%IMAGE_TAG%
                    '''
                }
            }
        }


    }

}
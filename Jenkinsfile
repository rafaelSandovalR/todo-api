pipeline {
    agent none

    environment {
        DOCKER_HUB_CREDS = credentials('dockerhub-creds')
        IMAGE_NAME = "rsandoval0408/todo-api"
    }

    stages {

        // --- STAGE 1: Run Tests ---
        // This stage runs inside a *temporary* Maven container
        stage('Run Tests') {
            agent {
                docker { image 'maven:3.9.6-eclipse-temurin-17' }
            }
            steps {
                // We use 'mvnw' (Maven Wrapper) to be safe
                sh 'chmod +x mvnw'
                sh './mvnw test'
            }
        }

        // --- STAGE 2: Build & Push Image ---
        // This stage runs inside a *temporary* Docker container
        stage('Build and Push Docker Image') {
            agent {
                docker { image 'docker:latest' }
            }
            steps {
                // 1. Log in to Docker Hub
                sh "echo $DOCKER_HUB_CREDS_PSW | docker login -u $DOCKER_HUB_CREDS_USR --password-stdin"

                // 2. Build the image
                sh "docker build -t ${IMAGE_NAME} ."

                // 3. Push the image
                sh "docker push ${IMAGE_NAME}"
            }
            post {
                always {
                    sh 'docker logout'
                }
            }
        }
    }
}
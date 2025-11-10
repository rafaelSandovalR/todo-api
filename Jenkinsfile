pipeline {
    // We set the "agent" to "none" at the top, because
    // each stage will define its own agent.
    agent none

    environment {
        DOCKER_HUB_CREDS = credentials('dockerhub-creds')
        IMAGE_NAME = "rsandoval0408/todo-api"
    }

    stages {
        // --- STAGE 1: Run Tests ---
        // This stage needs Java and Maven.
        stage('Run Tests') {
            agent {
                // Use an official Maven image. It has Java and Maven pre-installed.
                docker { image 'maven:3.9.6-eclipse-temurin-17' }
            }
            steps {
                // We're inside a container, so we just run the Maven command
                sh 'mvn test'
            }
        }

        // --- STAGE 2: Build & Push Image ---
        // This stage needs the Docker client.
        stage('Build and Push Docker Image') {
            // It only runs if 'Run Tests' was successful
            agent {
                // Use an official Docker image. It has the Docker client pre-installed.
                docker { image 'docker:latest' }
            }
            steps {
                // Because we mounted the "docker.sock" to our Jenkins container,
                // *this* container can "borrow" that connection and
                // control the host's Docker engine.

                // 1. Log in to Docker Hub
                // We use the credentials we stored in Jenkins
                sh "echo $DOCKER_HUB_CREDS_PSW | docker login -u $DOCKER_HUB_CREDS_USR --password-stdin"

                // 2. Build the image
                sh "docker build -t ${IMAGE_NAME} ."

                // 3. Push the image
                sh "docker push ${IMAGE_NAME}"
            }
            post {
                // Always log out for security
                always {
                    sh 'docker logout'
                }
            }
        }
    }
}
pipeline {
    // 1. "agent" defines where this pipeline runs.
    // "any" means it will run on the main Jenkins server.
    agent any

    // 2. "environment" sets up variables
    environment {
        // Get the credentials we just stored in Jenkins
        DOCKER_HUB_CREDS = credentials('dockerhub-creds')

        // Define our image name
        IMAGE_NAME = "rsandoval0408/todo-api"
    }

    // 3. "stages" are the steps of our assembly line
    stages {
        stage('Run Tests') {
            steps {
                // The Jenkins image we're using (lts-jdk17) already has Java.
                // We just need to make the Maven wrapper executable.
                sh 'chmod +x mvnw'

                // Run all the JUnit tests
                sh './mvnw test'
            }
        }

        stage('Build Docker Image') {
            // This stage only runs if 'Run Tests' was successful.
            steps {
                // Because we mounted the docker.sock, Jenkins can
                // run docker commands directly.
                sh "docker build -t ${IMAGE_NAME} ."
            }
        }

        stage('Push to Docker Hub') {
            // This stage only runs if 'Build Docker Image' was successful.
            steps {
                // Use the environment variables from the credentials() helper
                // to log in. $DOCKER_HUB_CREDS_PSW is the password.
                sh "echo $DOCKER_HUB_CREDS_PSW | docker login -u $DOCKER_HUB_CREDS_USR --password-stdin"

                // Push the image
                sh "docker push ${IMAGE_NAME}"
            }
            post {
                // "post" runs after the stage, no matter what.
                // Always log out for security.
                always {
                    sh 'docker logout'
                }
            }
        }
    }
}
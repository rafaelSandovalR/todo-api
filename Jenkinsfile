pipeline {
    // 1. Run on the main agent (our Jenkins server)
    agent any

    // 2. Define the tools we just configured in the GUI
    tools {
        jdk 'jdk17'      // This tells the pipeline: "Use the JDK we named 'jdk17'"
        dockerTool 'docker-latest' // "Use the Docker tool we named 'docker-latest'"
    }

    environment {
        IMAGE_NAME = "rsandoval0408/todo-api"
    }

    stages {

        stage('Run Tests') {
            steps {
                // Now that we have a JDK, we can use Maven
                sh 'chmod +x mvnw'
                sh './mvnw test'
            }
        }

        stage('Build and Push Docker Image') {
            // This stage only runs if 'Run Tests' was successful
            steps {
                // Now we can use the 'docker' command because
                // our Jenkins server has it (from the 'tools' block)

                // We wrap our secure steps in the withCredentials helper
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {

                    // 1. Log in to Docker Hub
                    // Use single quotes to securely pass shell variables
                    sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'

                    // 1. Build the image using docker-compose.yml blueprint
                    sh 'docker-compose build'

                    // Use double quotes below to allow Groovy interpolation
                    // 2. Tag the specific image that Compose just built
                    sh "docker tag workspace-app ${IMAGE_NAME}:latest"

                    // 3. Push the image
                    sh "docker push ${IMAGE_NAME}:latest"
                }
            }
            post {
                always {
                    sh 'docker logout'
                    sh 'docker-compose down'
                }
            }
        }
    }
}
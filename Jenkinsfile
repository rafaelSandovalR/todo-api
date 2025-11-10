pipeline {
    // 1. Run on the main agent (our Jenkins server)
    agent any

    // 2. Define the tools we just configured in the GUI
    tools {
        jdk 'jdk17'      // This tells the pipeline: "Use the JDK we named 'jdk17'"
        dockerTool 'docker-latest' // "Use the Docker tool we named 'docker-latest'"
    }

    environment {
        DOCKER_HUB_CREDS = credentials('dockerhub-creds')
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
                    // These variables are now securely provided by Jenkins
                    // and will not be printed to the log.
                    sh "echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin"

                    // 2. Build the image
                    sh "docker build -t ${IMAGE_NAME} ."

                    // 3. Push the image
                    sh "docker push ${IMAGE_NAME}"
                }
            }
            post {
                always {
                    sh 'docker logout'
                }
            }
        }
    }
}
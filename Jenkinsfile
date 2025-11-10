pipeline {
    // 1. We will use ONE agent for the WHOLE pipeline.
    // This agent is a container from the 'docker:latest' image.
    // This gives us the 'docker' command.
    agent {
        docker {
            image 'docker:latest'
            // This is the "garage door opener" that lets this
            // container talk to your host's Docker engine.
            args '-v /var/run/docker.sock:/var/run/docker.sock'
        }
    }

    // 2. Environment variables are the same.
    environment {
        DOCKER_HUB_CREDS = credentials('dockerhub-creds')
        IMAGE_NAME = "rsandoval0408/todo-api"
    }

    stages {

        // --- STAGE 1: Install Tools & Run Tests ---
        stage('Test') {
            steps {
                // The 'docker:latest' container is very minimal.
                // It does NOT have Java or Maven. So, we install them!

                // 'apk' is the Linux package manager for this container
                sh 'apk update'
                sh 'apk add maven'
                sh 'apk add openjdk17-jre' // We just need the JRE to run tests

                // Now that Maven is installed, we can run the test.
                sh 'mvn test'
            }
        }

        // --- STAGE 2: Build & Push Image ---
        stage('Build and Push') {
            // This stage only runs if 'Test' was successful
            steps {
                // Now we can use the 'docker' command because
                // our agent (the 'docker:latest' container) has it.

                // 1. Log in
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
pipeline {
    agent any
    tools {
        jdk 'jdk17'
        dockerTool 'docker-latest'
    }

    environment {
        IMAGE_NAME = "rsandoval0408/todo-api"
        TEST_DB_NAME = "todo_test"
    }

    stages {
        // --- STAGE 1: Unit Tests ---
        stage('Run Unit Tests') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw test -Dtest=TaskControllerUnitTests'
            }
        }

        // --- STAGE 2: Integration Tests ---
        stage('Run Integration Tests') {
            environment {
                SPRING_DATASOURCE_URL = "jdbc:postgresql://host.docker.internal:5432/${TEST_DB_NAME}"
            }
            steps {
                // Start Build (db only required for integration test)
                sh 'docker-compose up -d db'
                sh 'sleep 10'
                //sh "docker exec my-postpres-db psql -U postgres -d postgres -c 'CREATE DATABASE ${TEST_DB_NAME};'"
                sh './mvnw test -Dtest=TaskApiIntegrationTests'
            }
            post {
                always {
                    // CRITICAL: Tears down DB so the next stage starts clean
                    sh 'docker-compose down -v'
                }
            }
        }

        // --- STAGE 3: Build & Push (Only runs if tests pass) ---
        stage('Build and Publish Image') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    // Login
                    sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'

                    // Finish Build (build app only since db is already built)
                    sh 'docker-compose build app'

                    // Tag and Push
                    sh "docker tag todo-api-pipeline-app ${IMAGE_NAME}:latest"
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
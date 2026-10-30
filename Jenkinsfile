pipeline {
    agent any

    environment {
        // Docker Hub repository for the image (username/repo)
        IMAGE   = 'sweetnslimybutter/calculator-miniproject'
        // Same Compose project name as a local "docker compose up", so re-runs update the stack in place
        PROJECT = 'calculator-miniproject'
        NOTIFY  = 'prakrititz.borah.iiitb@gmail.com'
    }

    // Fallback to the webhook: check GitHub for new commits every ~2 minutes
    triggers {
        pollSCM('H/2 * * * *')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                bat 'mvn -B clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                bat 'mvn -B test'
            }
            post {
                always {
                    // Publish JUnit XML even when tests fail
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Docker Build') {
            steps {
                bat 'docker build -t %IMAGE%:%BUILD_NUMBER% -t %IMAGE%:latest .'
            }
        }

        stage('Push') {
            steps {
                // Token comes from Jenkins Credentials, never from the repo
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds',
                                                  usernameVariable: 'DH_USER',
                                                  passwordVariable: 'DH_PASS')]) {
                    bat 'echo %DH_PASS%| docker login -u %DH_USER% --password-stdin'
                    bat 'docker push %IMAGE%:%BUILD_NUMBER%'
                    bat 'docker push %IMAGE%:latest'
                    bat 'docker logout'
                }
            }
        }

        stage('Deploy') {
            steps {
                // CALC_IMAGE tells docker-compose.yml which image to run
                withEnv(["CALC_IMAGE=${env.IMAGE}:${env.BUILD_NUMBER}"]) {
                    bat 'docker compose -p %PROJECT% up -d --remove-orphans'
                }
                // Fail the build unless the app reports UP through nginx
                powershell '''
                    for ($i = 0; $i -lt 20; $i++) {
                        try {
                            if ((Invoke-RestMethod http://localhost/actuator/health).status -eq "UP") { exit 0 }
                        } catch {}
                        Start-Sleep 3
                    }
                    Write-Error "App did not become healthy"
                    exit 1
                '''
            }
        }
    }

    post {
        success {
            mail to: "${env.NOTIFY}",
                 subject: "SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: "Build passed and deployed image ${env.IMAGE}:${env.BUILD_NUMBER}.\n${env.BUILD_URL}"
        }
        failure {
            mail to: "${env.NOTIFY}",
                 subject: "FAILED: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: "Build failed. See the console output: ${env.BUILD_URL}console"
        }
    }
}

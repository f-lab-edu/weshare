pipeline {
    agent any

    tools {
        jdk("java17")
    }

    stages {
        // Checkout Git repository
        stage('Checkout Git') {
            steps {
                checkout scm
                echo 'dasfdasfdsafadsfsdafdsafdsafasd'
            }
        }

        stage('Inject .env') {
            steps {
                script {
                    def workspace = pwd()
                    def envFilePath = "${workspace}/.env"

                    def jenkinsEnvFilePath = '/var/lib/jenkins/.env'
                    sh "cp ${jenkinsEnvFilePath} ${envFilePath}"
                }
            }
        }

        stage('build') {
            steps {
                echo 'build 수행'
                sh "./gradlew clean build"
            }
        }

        stage('Docker Build') {
            steps {
                script {
                    echo 'docker image 빌드'
                    docker.build("weshare:latest")
                }
            }
        }

        stage('Verify Local Image') {
            steps {
                script {
                    def imageExists = sh(script: "docker images -q weshare:latest", returnStatus: true) == 0
                    if (imageExists) {
                        echo "Docker image weshare:latest exists locally."
                    } else {
                        error "Docker image weshare:latest does not exist locally."
                    }
                }
            }
        }

    }
    post {
        success {
            // 빌드 성공 후 수행할 작업
            echo 'Build 성공'
        }
        failure {
            // 빌드 실패 후 수행할 작업
            echo 'Build 실패'
        }
    }
}

pipeline {
    agent any

    tools {
        jdk("java17")
    }

    stages {
        script {
            // BASIC
            PROJECT_NAME = 'weshare'

            // DOCKER
            DOCKER_HUB_URL = 'registry.hub.docker.com'
            DOCKER_HUB_FULL_URL = 'https://' + DOCKER_HUB_URL
            DOCKER_HUB_CREDENTIAL_ID = 'dockerhub-token'
            DOCKER_IMAGE_NAME = PROJECT_NAME
        }

        // Checkout Git repository
        stage('Checkout Git') {
            steps {
                checkout scm
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

        stage('Build & Push Docker Image') {
            steps {
                echo 'Build & Push Docker Image'
                withCredentials([usernamePassword(
                        credentialsId: DOCKER_HUB_CREDENTIAL_ID,
                        usernameVariable: 'DOCKER_HUB_ID',
                        passwordVariable: 'DOCKER_HUB_PW')]) {

                    script {
                        docker.withRegistry(DOCKER_HUB_FULL_URL,
                                DOCKER_HUB_CREDENTIAL_ID) {
                            app = docker.build(DOCKER_HUB_ID + '/' + DOCKER_IMAGE_NAME)
                            echo 'docker build 완료'
                            app.push(env.BUILD_ID)
                            ehco 'docker image push by weshare ${env.BUILD_ID}'
                            app.push('latest')
                            ehco 'docker image push by weshare latset'
                        }
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

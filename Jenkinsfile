pipeline {
    agent any

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
        post {
            success {
                // 빌드 성공 후 수행할 작업
                echo 'Build succeeded!'
            }
            failure {
                // 빌드 실패 후 수행할 작업
                echo 'Build failed!'
            }
        }
    }
}

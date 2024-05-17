pipeline {
    agent any

    stages {
        stage('github-clone') {
            steps {
                git branch: 'main', credentialsId: 'github-token', url: '{REPOSITORY URL}'
            }
        }
    }
}

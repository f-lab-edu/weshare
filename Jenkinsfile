pipeline {
    agent any

    stages {
        stage('github-clone') {
            steps {
                git branch: 'main', credentialsId: 'github_token', url: '{REPOSITORY URL}'
            }
        }
    }
}

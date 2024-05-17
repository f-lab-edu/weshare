pipeline {
    agent any

    stages {
        stage('github-clone') {
            steps {
                def branchName = env.GIT_BRANCH
                git branch: branchName, credentialsId: 'github-token', url: '{REPOSITORY URL}'
                echo "Checking out branch:"
            }
        }
    }
}

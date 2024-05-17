pipeline {
    agent any

    stages {
        // Checkout Git repository
        stage('Checkout Git') {
            steps {
                git branch: env.GIT_BRANCH, url: 'https://github.com/f-lab-edu/weshare.git'
                echo 'please please'
            }
        }
    }
}

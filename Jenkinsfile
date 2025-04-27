pipeline {
    agent any
    tools {
        maven 'M3'
    }
    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/ellie-byrne/GoodEats.git'
            }
        }
        stage('Build') {
            steps {
                sh 'mvn clean install'
            }
        }
//         stage('Test') {
//             steps {
//                 // Run Maven tests when we do them
//                 sh 'mvn test'
//             }
//         }
        stage('Deploy') {
            steps {
                sh 'mvn spring-boot:run'
            }
        }
    }
    post {
        success {
            echo 'Build succeeded!'
        }
        failure {
            echo 'Build failed.'
        }
    }
}

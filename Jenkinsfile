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
        stage('Check Java') {
            steps {
                sh 'java -version'
            }
        }
        stage('Build') {
            steps {
                sh 'mvn -Dmaven.compiler.source=17 -Dmaven.compiler.target=17 clean install'
            }
        }
        stage('Deploy') {
            steps {
                sh 'mvn org.springframework.boot:spring-boot-maven-plugin:run -Dspring-boot.run.arguments="--server.port=8081"'
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

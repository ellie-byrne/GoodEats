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
                sh 'mvn -Dmaven.compiler.source=17 -Dmaven.compiler.target=17 clean package -DskipTests'
            }
        }
        stage('Build Docker Image') {
            steps {
                sh '''
                cat > Dockerfile <<EOF
FROM eclipse-temurin:17-jdk
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar", "--server.port=8081"]
EOF
                '''
                sh 'docker build -t goodeats:latest .'
            }
        }
        stage('Deploy') {
            steps {
                sh 'docker stop goodeats || true'
                sh 'docker rm goodeats || true'
                sh 'docker run -d --name goodeats -p 8081:8081 --restart=always goodeats:latest'
            }
        }
    }
    post {
        success {
            echo 'Build and deployment succeeded!'
        }
        failure {
            echo 'Build or deployment failed.'
        }
    }
}

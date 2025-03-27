pipeline {
    agent any

    environment {
        MAVEN_HOME = tool name: 'M3', type: 'ToolLocation'
    }

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/ellie-byrne/GoodEats.git'
            }
        }

        stage('Build') {
            steps {
                script {
                    sh "'${MAVEN_HOME}/bin/mvn' clean install"
                }
            }
        }

//         stage('Test') {
//             steps {
//                 script {
//                     // Run Maven tests when we do them
//                     sh "'${MAVEN_HOME}/bin/mvn' test"
//                 }
//             }
//         }

        stage('Deploy') {
            steps {
                script {
                    sh "'${MAVEN_HOME}/bin/mvn' spring-boot:run"
                }
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

pipeline {
    agent any
    
    parameters {
        booleanParam(name: 'SKIP_TESTS', defaultValue: false, description: 'Skip running tests')
        booleanParam(name: 'DEBUG_MODE', defaultValue: false, description: 'Run Maven in debug mode')
    }
    
    stages {
        stage('Checkout') {
            steps {
                sh 'rm -rf GoodEats'
                sh 'git clone https://github.com/ellie-byrne/GoodEats.git'
            }
        }
        
        stage('Show Test Reports') {
            steps {
                sh 'find GoodEats -name "surefire-reports" || echo "No test reports found yet"'
            }
        }
        
        stage('Build') {
            agent {
                docker {
                    image 'maven:3.8-openjdk-17'
                    reuseNode true
                }
            }
            steps {
                dir('GoodEats/backend') {
                    script {
                        if (params.SKIP_TESTS) {
                            sh 'mvn -Dmaven.compiler.source=17 -Dmaven.compiler.target=17 clean package -DskipTests'
                            echo 'Tests were skipped'
                        } else if (params.DEBUG_MODE) {
                            sh 'mvn -Dmaven.compiler.source=17 -Dmaven.compiler.target=17 clean package -X -e'
                            echo 'Built with debug output'
                        } else {
                            sh 'mvn -Dmaven.compiler.source=17 -Dmaven.compiler.target=17 clean package'
                        }
                    }
                    sh 'mkdir -p target/surefire-reports || true'
                    archiveArtifacts allowEmptyArchive: true, artifacts: 'target/surefire-reports/**/*', fingerprint: true
                    
                    sh '''
                        if [ -d target/surefire-reports ]; then
                            echo "Failed Tests:"
                            grep -l "failures=\"[1-9]" target/surefire-reports/*.xml | xargs -r grep -l "<failure" || echo "No test failures found in XML reports"
                            grep -l "errors=\"[1-9]" target/surefire-reports/*.xml | xargs -r grep -l "<error" || echo "No test errors found in XML reports"
                        else
                            echo "No test reports directory found"
                        fi
                    '''
                }
            }
        }
        
        stage('Deploy') {
            when {
                anyOf {
                    expression { params.SKIP_TESTS == true }
                    expression { currentBuild.resultIsBetterOrEqualTo('SUCCESS') }
                }
            }
            steps {
                script {
                    def environment = params.ENVIRONMENT
                    echo "Deploying to ${environment} environment"
                    
                    dir('GoodEats/backend') {
                        sh """
                            docker build -t goodeats:${BUILD_NUMBER} .
                            docker tag goodeats:${BUILD_NUMBER} goodeats:latest
                            docker stop goodeats || true
                            docker rm goodeats || true
                            docker run -d --name goodeats -p 5000:5000 goodeats:latest
                        """
                    }
                }
            }
        }
    }
    
    post {
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}

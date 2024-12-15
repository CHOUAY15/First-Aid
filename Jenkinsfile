pipeline {
    agent any

    environment {
        BRANCH_DEV = 'origin/develop'
        BRANCH_PROD = 'origin/main'
        NEXUS_PROXY = "http://master:9082"
        NEXUS_PRIVATE = "http://master:9083"
        NEXUS_CREDENTIALS_ID = "nexus-credentials"
        SERVICES = "config-service,discovery-service,gateway-service,participant-service,training-service"
        TEST_SERVER = '192.168.11.138'
        TEST_SERVER_USER = 'chouay'
        PROJECT_PATH = '/home/chouay/first-aid'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    def changedFiles = sh(script: "git diff --name-only HEAD~1 HEAD", returnStdout: true).trim()
                    env.MODIFIED_SERVICES = SERVICES.split(',').findAll { service ->
                        changedFiles.contains(service)
                    }.join(',')
                    echo "Modified services: ${env.MODIFIED_SERVICES}"
                }
            }
        }

        stage('Tests') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Sonarqube Analysis') {
            steps {
                script {
                    withSonarQubeEnv('sonar-server') {
                        sh "mvn sonar:sonar -Dintegration-tests.skip=true -Dmaven.test.failure.ignore=true"
                    }
                    timeout(time: 1, unit: 'MINUTES') {
                        def qg = waitForQualityGate()
                        if (qg.status != 'OK') {
                            error "Pipeline aborted due to quality gate failure: ${qg.status}"
                        }
                    }
                }
            }
        }
        stage('Maven Build and Package') {
            steps {
                script {
                    sh 'mvn clean package -DskipTests'
                }
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: true
                }
                failure {
                    echo 'Maven build failed'
                }
            }
        }

        stage('Docker Build and Push to Nexus') {
            steps {
                script {
                    def envName = env.GIT_BRANCH == BRANCH_PROD ? "prod" : "dev"
                    def modifiedServicesList = env.MODIFIED_SERVICES ? env.MODIFIED_SERVICES.split(',') : []

                    modifiedServicesList.each { service ->
                        dir(service) {
                            def version = getEnvVersion(service, envName)
                            echo "Building Docker image for ${service} with version ${version}"

                            withCredentials([usernamePassword(credentialsId: "${NEXUS_CREDENTIALS_ID}",
                                    usernameVariable: 'USER',
                                    passwordVariable: 'PASSWORD')]) {
                                sh """
                                    echo \$PASSWORD | docker login -u \$USER --password-stdin ${NEXUS_PRIVATE}
                                    docker build -t ${NEXUS_PRIVATE}/${service}:${version} .
                                    docker push ${NEXUS_PRIVATE}/${service}:${version}
                                """
                            }
                        }
                    }
                }
            }
        }
        stage('Deploy to Test Server') {
            steps {
                script {
                    // First test connectivity
                    echo "Testing connectivity to ${TEST_SERVER}"

                    try {
                        sh "ping -c 1 ${TEST_SERVER}"
                    } catch (Exception e) {
                        error "Cannot ping ${TEST_SERVER}. Error: ${e.message}"
                    }

                    // Test SSH connection
                    sshagent(credentials: ['ssh-credentials-id']) {
                        try {
                            sh """
                        ssh -o StrictHostKeyChecking=no ${TEST_SERVER_USER}@${TEST_SERVER} 'echo "SSH Connection successful"'
                    """
                        } catch (Exception e) {
                            error "SSH connection failed. Error: ${e.message}"
                        }

                        // If we get here, connectivity is working
                        echo "Connectivity tests passed. Proceeding with deployment..."

                        def envName = env.GIT_BRANCH == BRANCH_PROD ? "prod" : "test"
                        def modifiedServicesList = env.MODIFIED_SERVICES ? env.MODIFIED_SERVICES.split(',') : []

                        modifiedServicesList.each { service ->
                            def version = getEnvVersion(service, envName)

                            // Debug information
                            echo "Deploying service: ${service}"
                            echo "Version: ${version}"
                            echo "Target directory: ${PROJECT_PATH}"

                            sh """
                        ssh -o StrictHostKeyChecking=no ${TEST_SERVER_USER}@${TEST_SERVER} '
                            echo "Current directory: \$(pwd)"
                            echo "Checking if target directory exists..."
                            if [ ! -d "${PROJECT_PATH}" ]; then
                                echo "Directory ${PROJECT_PATH} does not exist!"
                                exit 1
                            fi
                            
                            echo "Changing to project directory..."
                            cd ${PROJECT_PATH}
                            
                            echo "Creating/Updating .env file..."
                            echo "NEXUS_PRIVATE=${NEXUS_PRIVATE}" > .env
                            echo "VERSION=${version}" >> .env
                            
                            echo "Checking docker status..."
                            docker info
                            
                            echo "Checking if docker-compose file exists..."
                            if [ ! -f "docker-compose.yml" ]; then
                                echo "docker-compose.yml not found!"
                                exit 1
                            fi
                            
                            echo "Docker compose version:"
                            docker-compose version
                            
                            echo "Stopping existing containers..."
                            docker-compose down || true
                            
                            echo "Pulling new images..."
                            docker-compose pull || true
                            
                            echo "Starting containers..."
                            docker-compose up -d
                            
                            echo "Container status:"
                            docker ps
                            
                            echo "Docker compose logs:"
                            docker-compose logs
                        '
                    """
                        }
                    }
                }
            }
            post {
                failure {
                    echo "Deployment stage failed. Check the logs above for details."
                }
                success {
                    echo "Deployment stage completed successfully."
                }
            }
        }


    }

}
def getEnvVersion(service, envName) {
    dir(service) {
        def pom = readMavenPom file: 'pom.xml'
        def artifactVersion = pom.version
        def gitCommit = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()

        def versionNumber = gitCommit ?
                "${artifactVersion}-${envName}.${env.BUILD_NUMBER}.${gitCommit.take(8)}" :
                "${artifactVersion}-${envName}.${env.BUILD_NUMBER}"

        echo "Build version for service ${service}: ${versionNumber}"
        return versionNumber
    }
}


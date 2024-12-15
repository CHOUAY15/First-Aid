pipeline {
    agent any

    environment {
        BRANCH_DEV = 'origin/develop'
        BRANCH_PROD = 'origin/main'
        NEXUS_PROXY = "http://master:9082"
        NEXUS_PRIVATE = "http://master:9083"
        NEXUS_CREDENTIALS_ID = "nexus-credentials"
        // Correctly define SERVICES as a comma-separated string
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
                    // Store modified services as a comma-separated string instead of a list
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
                    // Build and package the project, skipping tests to optimize the process
                    sh 'mvn clean package -DskipTests'
                }
            }
            post {
                success {
                    // Archive the generated .jar files for later use (deployment or inspection)
                    archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: true
                }
                failure {
                    // Handle failures if necessary (e.g., notifying the team)
                    echo 'Maven build failed'
                }
            }
        }

        stage('Docker Build and Push to Nexus') {
            steps {
                script {
                    def envName = env.GIT_BRANCH == BRANCH_PROD ? "prod" : "dev"

                    // Split the string back into an array when needed
                    def modifiedServicesList = env.MODIFIED_SERVICES ? env.MODIFIED_SERVICES.split(',') : []

                    modifiedServicesList.each { service ->
                        dir(service) {  // Change to service directory
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
                    envName = "test"
                    if (env.GIT_BRANCH == BRANCH_PROD) {
                        envName = "prod"
                    }

                    // Iterate through modified services
                    env.MODIFIED_SERVICES.each { service ->
                        def version = getEnvVersion(service, envName)

                        // Deploy updated service
                        sshagent(credentials: ['ssh-credentials-id']) {
                            // Copy updated docker-compose.yml from source code to test server
                            sh """
                        scp -o StrictHostKeyChecking=no -i ${SSH_KEY} docker-compose.yml ${TEST_SERVER_SSH}:/home/chouay/first-aid/docker-compose.yml
                        ssh -o StrictHostKeyChecking=no -i ${SSH_KEY} ${TEST_SERVER_SSH} '
                            cd /home/chouay/first-aid/
                            export NEXUS_PRIVATE=${NEXUS_PRIVATE}
                            export VERSION=${version}
                            docker-compose down || true
                            docker-compose pull
                            docker-compose up -d
                        '
                    """
                        }
                    }
                }
            }
        }

    }

    post {
        success {
            echo 'Deployment successful!'
        }
        failure {
            echo 'Deployment failed!'
        }
    }
}
def getEnvVersion(service, envName) {
    dir(service) {  // Change to service directory to read the correct pom.xml
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


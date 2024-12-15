pipeline {
    agent any

    environment {
        BRANCH_DEV = 'origin/develop'
        BRANCH_PROD = 'origin/main'
        NEXUS_PROXY = "http://192.168.11.137:9082"
        NEXUS_PRIVATE = "http://192.168.11.137:9083"
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
                    sshagent(credentials: ['ssh-credentials-id']) {
                        def envName = env.GIT_BRANCH == BRANCH_PROD ? "prod" : "test"
                        def modifiedServicesList = env.MODIFIED_SERVICES ? env.MODIFIED_SERVICES.split(',') : []

                        // Modification de la partie authentification
                        withCredentials([usernamePassword(credentialsId: "${NEXUS_CREDENTIALS_ID}",
                                usernameVariable: 'NEXUS_USERNAME',
                                passwordVariable: 'NEXUS_PASSWORD')]) {

                            // Ensure all credential variables are used with proper scoping
                            sh """
                        ssh -o StrictHostKeyChecking=no ${TEST_SERVER_USER}@${TEST_SERVER} \
                        "docker login -u '${NEXUS_USERNAME}' -p '${NEXUS_PASSWORD}' ${NEXUS_PRIVATE}"
                    """

                            modifiedServicesList.each { service ->
                                def version = getEnvVersion(service, envName)

                                sh """
                            ssh -o StrictHostKeyChecking=no ${TEST_SERVER_USER}@${TEST_SERVER} '
                                set -x
                                cd ${PROJECT_PATH}
                                
                                # Create .env file with correct syntax
                                echo "NEXUS_PRIVATE=${NEXUS_PRIVATE}" > .env
                                echo "VERSION=${version}" >> .env
                                
                                # Debug: Show .env contents
                                echo "Contents of .env:"
                                cat .env
                                
                                docker-compose down || true
                                docker-compose rm -f || true
                                docker-compose pull || exit 1
                                docker-compose up --no-start || exit 1
                                docker-compose up -d
                                
                                sleep 10
                                
                                echo "Container status:"
                                docker ps -a
                                
                                echo "Docker compose logs:"
                                docker-compose logs
                                
                                RUNNING_CONTAINERS=\$(docker-compose ps --services --filter "status=running" | wc -l)
                                TOTAL_SERVICES=\$(docker-compose config --services | wc -l)
                                
                                echo "Running containers: \$RUNNING_CONTAINERS out of \$TOTAL_SERVICES"
                                
                                if [ "\$RUNNING_CONTAINERS" -lt "\$TOTAL_SERVICES" ]; then
                                    echo "Not all services are running!"
                                    exit 1
                                fi
                            '
                        """
                            }
                        }
                    }
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


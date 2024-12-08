pipeline {
    agent any
    environment {
        BRANCH_DEV = 'origin/develop'
        BRANCH_PROD = 'origin/main'
        NEXUS_PROXY = "http://master:9082"
        NEXUS_PRIVATE = "http://master:9083"
        NEXUS_CREDENTIALS_ID = "nexus-credentials"
        SERVICES = ['config-service', 'discovery-service', 'gateway-service', 'participant-service', 'training-service']
    }

        stages {
            stage('Checkout') {
                steps {
                    checkout scm
                    script {
                        def changedFiles = sh(script: "git diff --name-only HEAD~1 HEAD", returnStdout: true).trim()
                        env.MODIFIED_SERVICES = SERVICES.findAll { service -> changedFiles.contains(service) }
                        echo "Services modifiés: ${env.MODIFIED_SERVICES}"
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
                        archiveArtifacts 'target/*.jar'
                    }
                }
            }
            stage('Docker Build and Push') {
                steps {
                    script {
                        def envName = env.GIT_BRANCH == BRANCHE_PROD ? "prod" : "dev"

                        withCredentials([usernamePassword(credentialsId: NEXUS_CREDENTIALS_ID,
                                usernameVariable: 'USER',
                                passwordVariable: 'PASSWORD')]) {
                            // Login to Nexus
                            sh "echo \$PASSWORD | docker login -u \$USER --password-stdin ${NEXUS_URL}"

                            env.MODIFIED_SERVICES.each { service ->
                                def version = getEnvVersion(service, envName)
                                echo "Building ${service} version ${version}"

                                sh """
                                docker build -t ${NEXUS_PRIVATE}/${service}:${version} ./${service}
                                docker push ${NEXUS_PRIVATE}/${service}:${version}
                                docker rmi ${NEXUS_PRIVATE}/${service}:${version}
                            """
                            }
                        }
                    }
                }
            }

            stage('Deploy to Test') {
                when { expression { env.GIT_BRANCH == BRANCHE_DEV } }
                steps {
                    script {
                        def envName = "dev"
                        env.MODIFIED_SERVICES.each { service ->
                            def version = getEnvVersion(service, envName)
                            sh """
                            sed -i 's|image: ${NEXUS_PRIVATE}/${service}:.*|image: ${NEXUS_PRIVATE}/${service}:${version}|g' docker-compose.test.yml
                        """
                        }

                        sshagent(['test-server-credentials']) {
                            sh """
                            ssh test-server 'cd /app && docker-compose -f docker-compose.test.yml down'
                            scp docker-compose.test.yml test-server:/app/
                            ssh test-server 'cd /app && docker-compose -f docker-compose.test.yml up -d'
                        """
                        }
                    }
                }
            }

            stage('Deploy to Production') {
                when { expression { env.GIT_BRANCH == BRANCHE_PROD } }
                steps {
                    input 'Deploy to Production?'
                    script {
                        def envName = "prod"
                        env.MODIFIED_SERVICES.each { service ->
                            def version = getEnvVersion(service, envName)
                            sh """
                            sed -i 's|image: ${NEXUS_PRIVATE}/${service}:.*|image: ${NEXUS_PRIVATE}/${service}:${version}|g' docker-compose.prod.yml
                        """
                        }

                        sshagent(['prod-server-credentials']) {
                            sh """
                            ssh prod-server 'cd /app && docker-compose -f docker-compose.prod.yml down'
                            scp docker-compose.prod.yml prod-server:/app/
                            ssh prod-server 'cd /app && docker-compose -f docker-compose.prod.yml up -d'
                        """
                        }
                    }
                }
            }
        }

        }
def getEnvVersion(service, envName) {
    def pom = readMavenPom file: "${service}/pom.xml"
    def artifactVersion = "${pom.version}"
    def gitCommit = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
    def versionNumber = "${artifactVersion}-${envName}.${env.BUILD_NUMBER}.${gitCommit.take(8)}"
    echo "Building version ${versionNumber} for ${service}"
    return versionNumber
}
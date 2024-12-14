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
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    def changedFiles = sh(script: "git diff --name-only HEAD~1 HEAD", returnStdout: true).trim()
                    // Split the SERVICES string into a list
                    env.MODIFIED_SERVICES = SERVICES.split(',').findAll { service -> changedFiles.contains(service) }
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
                    envName = "dev"
                    if (env.GIT_BRANCH == BRANCH_PROD) {
                        envName = "prod"
                    }

                    // Build and push only the modified services to Nexus
                    env.MODIFIED_SERVICES.each { service ->
                        def version = getEnvVersion(service, envName)
                        echo "Building Docker image for ${service} with version ${version}"

                        withCredentials([usernamePassword(credentialsId: "${NEXUS_CREDENTIALS_ID}", usernameVariable: 'USER', passwordVariable: 'PASSWORD')]) {
                            sh 'echo $PASSWORD | docker login -u $USER --password-stdin $NEXUS_PRIVATE'
                            sh "docker build -t ${NEXUS_PRIVATE}/${service}:${version} ./${service}"
                            sh "docker push ${NEXUS_PRIVATE}/${service}:${version}"
                        }
                    }
                }

            }
        }


    }
}
def getEnvVersion(service, envName) {
    // Read Maven POM file to get the version
    def pom = readMavenPom file: 'pom.xml'
    // Get the current development version from POM
    def artifactVersion = "${pom.version}"

    // Get the current git commit hash
    def gitCommit = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()

    // Construct version number based on git commit and environment
    def versionNumber
    if (gitCommit == null || gitCommit.isEmpty()) {
        // If no commit hash found, just use the environment and build number
        versionNumber = "${artifactVersion}-${envName}.${env.BUILD_NUMBER}"
    } else {
        // Use shortened git commit hash if available
        versionNumber = "${artifactVersion}-${envName}.${env.BUILD_NUMBER}.${gitCommit.take(8)}"
    }

    // Print the version number for debugging
    echo "Build version for service ${service}: ${versionNumber}"

    return versionNumber
}


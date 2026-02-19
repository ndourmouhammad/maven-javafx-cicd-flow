pipeline {
    agent any

    environment {
        MAVEN_TOOL_NAME = 'M3'
        NEXUS_SETTINGS_ID = 'my-nexus-settings'
    }

    stages {
        stage('Initialisation') {
            steps {
                script {
                    def mvnHome = tool "${MAVEN_TOOL_NAME}"
                    env.MAVEN_HOME = mvnHome
                }
            }
        }

        stage('Checkout') { steps { checkout scm } }

        stage('Build & Test') {
            steps { sh "${env.MAVEN_HOME}/bin/mvn clean package" }
        }

        stage('Analyse SonarQube') {
            steps {
                script {
                    try {
                        withSonarQubeEnv('SonarQube') {
                            sh "${env.MAVEN_HOME}/bin/mvn sonar:sonar"
                        }
                    } catch (e) { echo "SonarQube échoué, on continue..." }
                }
            }
        }

        stage('Deploy to Nexus') {
            steps {
                configFileProvider([configFile(fileId: "${NEXUS_SETTINGS_ID}", variable: 'MAVEN_SETTINGS')]) {
                    sh "${env.MAVEN_HOME}/bin/mvn deploy -s $MAVEN_SETTINGS -DskipTests"
                }
            }
        }

        stage('Setup SSH Host Key') {
            steps {
                sh '''
                    # Ajouter le serveur aux known_hosts
                    mkdir -p ~/.ssh
                    ssh-keyscan -H 13.62.126.153 >> ~/.ssh/known_hosts
                    # Vérifier la connexion SSH
                    ssh -o StrictHostKeyChecking=accept-new user@13.62.126.154 "echo SSH connection OK"
                '''
            }
        }
    }
}
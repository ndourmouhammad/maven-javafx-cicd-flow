pipeline {
    agent any

    environment {
        // Doit correspondre au nom dans Jenkins > Admin > Tools
        MAVEN_HOME = tool 'maven-3.9.12'
        // ID du fichier XML dans Jenkins > Admin > Managed Files
        NEXUS_SETTINGS_ID = 'my-nexus-settings'
    }

    stages {
        stage('Checkout') {
            steps {
                // Jenkins récupère automatiquement le code si configuré via SCM (Github)
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                script {
                    def mvnCmd = isUnix() ? "${MAVEN_HOME}/bin/mvn" : "${MAVEN_HOME}\\bin\\mvn.cmd"
                    if (isUnix()) {
                        sh "${mvnCmd} clean package"
                    } else {
                        bat "${mvnCmd} clean package"
                    }
                }
            }
        }

        stage('Analyse SonarQube') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    script {
                        def mvnCmd = isUnix() ? "${MAVEN_HOME}/bin/mvn" : "${MAVEN_HOME}\\bin\\mvn.cmd"
                        if (isUnix()) {
                            sh "${mvnCmd} sonar:sonar"
                        } else {
                            bat "${mvnCmd} sonar:sonar"
                        }
                    }
                }
            }
        }

        stage('Deploy to Nexus') {
            steps {
                configFileProvider([configFile(fileId: "${NEXUS_SETTINGS_ID}", variable: 'MAVEN_SETTINGS')]) {
                    script {
                        def mvnCmd = isUnix() ? "${MAVEN_HOME}/bin/mvn" : "${MAVEN_HOME}\\bin\\mvn.cmd"
                        if (isUnix()) {
                            sh "${mvnCmd} deploy -s $MAVEN_SETTINGS -DskipTests"
                        } else {
                            bat "${mvnCmd} deploy -s %MAVEN_SETTINGS% -DskipTests"
                        }
                    }
                }
            }
        }

        stage('Deploy with Ansible') {
            steps {
                script {
                    if (isUnix()) {
                        sh "ansible-playbook -i ansible/inventory.ini ansible/deploy.yml -v"
                    } else {
                        bat "ansible-playbook -i ansible/inventory.ini ansible/deploy.yml -v"
                    }
                }
            }
        }
    }

    post {
        always {
            echo "Pipeline terminé."
        }
        success {
            echo "Le déploiement a réussi !"
        }
        failure {
            echo "Une erreur est survenue pendant le pipeline."
        }
    }
}
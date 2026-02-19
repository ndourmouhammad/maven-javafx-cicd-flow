pipeline {
    agent any

    environment {
        // ASSUREZ-VOUS QUE CE NOM CORRESPOND EXACTEMENT À CELUI DANS Jenkins > Administrer Jenkins > Global Tool Configuration
        MAVEN_TOOL_NAME = 'maven-3.9.12'
        // ID du fichier XML dans Jenkins > Admin > Managed Files
        NEXUS_SETTINGS_ID = 'my-nexus-settings'
    }

    stages {
        stage('Initialisation') {
            steps {
                script {
                    echo "Début du pipeline..."
                    try {
                        def mvnHome = tool "${MAVEN_TOOL_NAME}"
                        env.MAVEN_HOME = mvnHome
                        echo "Maven trouvé à l'emplacement : ${env.MAVEN_HOME}"
                    } catch (Exception e) {
                        error "ERREUR : L'outil Maven nommé '${MAVEN_TOOL_NAME}' n'est pas configuré dans Jenkins. Veuillez le vérifier dans 'Global Tool Configuration'."
                    }
                }
            }
        }

        stage('Checkout') {
            steps {
                echo "Récupération du code depuis le SCM..."
                checkout scm
                echo "Récupération terminée."
            }
        }

        stage('Build & Test') {
            steps {
                script {
                    echo "Démarrage du Build & Test..."
                    def mvnCmd = isUnix() ? "${env.MAVEN_HOME}/bin/mvn" : "${env.MAVEN_HOME}\\bin\\mvn.cmd"
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
                script {
                    echo "Démarrage de l'analyse SonarQube..."
                    try {
                        withSonarQubeEnv('SonarQube') {
                            def mvnCmd = isUnix() ? "${env.MAVEN_HOME}/bin/mvn" : "${env.MAVEN_HOME}\\bin\\mvn.cmd"
                            if (isUnix()) {
                                sh "${mvnCmd} sonar:sonar"
                            } else {
                                bat "${mvnCmd} sonar:sonar"
                            }
                        }
                    } catch (Exception e) {
                        echo "Attention : Échec de la connexion à SonarQube. Vérifiez la configuration du serveur dans Jenkins."
                        throw e
                    }
                }
            }
        }

        stage('Deploy to Nexus') {
            steps {
                script {
                    echo "Démarrage du déploiement vers Nexus..."
                    try {
                        configFileProvider([configFile(fileId: "${NEXUS_SETTINGS_ID}", variable: 'MAVEN_SETTINGS')]) {
                            def mvnCmd = isUnix() ? "${env.MAVEN_HOME}/bin/mvn" : "${env.MAVEN_HOME}\\bin\\mvn.cmd"
                            if (isUnix()) {
                                sh "${mvnCmd} deploy -s $MAVEN_SETTINGS -DskipTests"
                            } else {
                                bat "${mvnCmd} deploy -s %MAVEN_SETTINGS% -DskipTests"
                            }
                        }
                    } catch (Exception e) {
                        echo "Erreur lors du déploiement Nexus. Vérifiez vos identifiants et le fichier 'my-nexus-settings'."
                        throw e
                    }
                }
            }
        }

        stage('Deploy with Ansible') {
            steps {
                script {
                    echo "Démarrage du déploiement Ansible..."
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
            echo "Une erreur critique est survenue. Veuillez consulter les logs de la console ci-dessous pour plus de détails."
        }
    }
}
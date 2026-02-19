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

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                sh "${env.MAVEN_HOME}/bin/mvn clean package"
            }
        }

        stage('Analyse SonarQube') {
            steps {
                script {
                    try {
                        withSonarQubeEnv('SonarQube') {
                            sh "${env.MAVEN_HOME}/bin/mvn sonar:sonar"
                        }
                    } catch (Exception e) {
                        echo "⚠️ SonarQube a échoué, mais on continue..."
                    }
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

        stage('Deploy with Ansible') {
            steps {
                script {
                    // On tente d'installer pip et ansible de manière robuste
                    sh """
                        # Mise à jour et installation des dépendances minimales
                        if ! command -v pip &> /dev/null; then
                            echo "Installation de pip..."
                            curl https://bootstrap.pypa.io/get-pip.py -o get-pip.py
                            python3 get-pip.py --user
                        fi

                        echo "Installation d'Ansible..."
                        python3 -m pip install --user ansible

                        echo "Exécution du Playbook..."
                        python3 -m ansible.playbook -i ansible/inventory.ini ansible/deploy.yml -v
                    """
                }
            }
        }
    }
}
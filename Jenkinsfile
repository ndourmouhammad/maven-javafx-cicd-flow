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
                    echo "🚀 Tentative d'exécution d'Ansible..."
                    sh """
                        # On s'assure que python3 et pip sont là
                        if ! command -v python3 &> /dev/null; then
                            echo "❌ Python3 n'est pas installé sur cet agent Jenkins."
                            exit 1
                        fi

                        # Installation/Mise à jour d'Ansible dans l'espace utilisateur
                        python3 -m pip install --user --upgrade pip
                        python3 -m pip install --user ansible

                        # On récupère le chemin exact des binaires de l'utilisateur
                        USER_BASE=\$(python3 -m site --user-base)
                        BIN_PATH="\$USER_BASE/bin"
                        
                        echo "🔍 Dossier des binaires : \$BIN_PATH"
                        ls -F "\$BIN_PATH" || echo "⚠️ Le dossier \$BIN_PATH est vide ou inaccessible"

                        # On ajoute explicitement ce dossier au PATH pour ce shell
                        export PATH="\$PATH:\$BIN_PATH"

                        echo "🔍 Vérification de la version d'Ansible :"
                        "\$BIN_PATH/ansible" --version || ansible --version

                        echo "🎬 Exécution du Playbook..."
                        "\$BIN_PATH/ansible-playbook" -i ansible/inventory.ini ansible/deploy.yml -v || ansible-playbook -i ansible/inventory.ini ansible/deploy.yml -v
                    """
                }
            }
        }
    }
}
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

        stage('Deploy with Ansible') {
                    steps {
                        // On ajoute ANSIBLE_HOST_KEY_CHECKING=False pour être 100% sûr que ça ne bloque pas
                        sh "ANSIBLE_HOST_KEY_CHECKING=False ansible-playbook -i ansible/inventory.ini ansible/deploy.yml -v"
                    }
                }
    }
}
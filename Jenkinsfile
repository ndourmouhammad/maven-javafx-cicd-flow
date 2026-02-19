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
                // 'ec2-ssh-key' est l'ID de ton credential Jenkins
                withCredentials([sshUserPrivateKey(credentialsId: 'ec2-ssh-key', keyFileVariable: 'PRIVATE_KEY')]) {
                    sh """
                        # On protège la clé (obligatoire pour SSH)
                        chmod 400 ${PRIVATE_KEY}

                        # On force Ansible à ignorer la vérification et à utiliser cette clé précise
                        export ANSIBLE_HOST_KEY_CHECKING=False

                        ansible-playbook -i ansible/inventory.ini ansible/deploy.yml \
                        -u ubuntu \
                        --private-key=${PRIVATE_KEY} \
                        --ssh-common-args='-o StrictHostKeyChecking=no -o PubkeyAcceptedAlgorithms=+ssh-rsa' \
                        -v
                    """
                }
            }
        }
    }
}
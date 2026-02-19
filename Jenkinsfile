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
                sshagent(['ec2-ssh-key']) {
                    sh """
                        # Create .ssh directory if it doesn't exist
                        mkdir -p ~/.ssh
                        chmod 700 ~/.ssh

                        # Scan the host key to avoid interactive prompt (Robust method)
                        ssh-keyscan -H 13.62.126.153 >> ~/.ssh/known_hosts 2>/dev/null || true

                        # Set environment variable to disable host key checking
                        export ANSIBLE_HOST_KEY_CHECKING=False

                        # Run playbook with strict host key checking disabled in SSH arguments
                        ansible-playbook -i ansible/inventory.ini ansible/deploy.yml \
                        -u ubuntu \
                        --extra-vars "ansible_ssh_common_args='-o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null'" \
                        -v
                    """
                }
            }
        }
    }
}
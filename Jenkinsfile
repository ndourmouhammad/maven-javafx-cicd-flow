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
                git branch: 'main',
                    credentialsId: 'github-ssh',
                    url: 'https://github.com/ndourmouhammad/maven-javafx-cicd-flow.git'
            }
        }

        stage('Build & Test') {
            steps {
                sh "${MAVEN_HOME}/bin/mvn clean package"
            }
        }

        stage('Analyse SonarQube') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh "${MAVEN_HOME}/bin/mvn sonar:sonar"
                }
            }
        }

        stage('Deploy to Nexus') {
            steps {
                configFileProvider([configFile(fileId: "${NEXUS_SETTINGS_ID}", variable: 'MAVEN_SETTINGS')]) {
                    sh "${MAVEN_HOME}/bin/mvn deploy -s $MAVEN_SETTINGS -DskipTests"
                }
            }
        }

        stage('Deploy with Ansible') {
            steps {
                sh "ansible-playbook -i ansible/inventory.ini ansible/deploy.yml -v"
            }
        }
    }
}
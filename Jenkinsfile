pipeline {
    agent any

    environment {
        MAVEN_HOME = tool 'maven-3.8.4'
        SONAR_QUBE_SERVER = 'SonarQube'
        NEXUS_CREDENTIALS_ID = 'nexus-credentials'
    }

    stages {
        stage('Checkout') {
            steps {
                // Jenkins récupère automatiquement le code si configuré via SCM
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh "'${MAVEN_HOME}/bin/mvn' clean package -DskipTests"
            }
        }

        stage('Test') {
            steps {
                sh "'${MAVEN_HOME}/bin/mvn' test"
            }
        }

        stage('Analyse SonarQube') {
            steps {
                withSonarQubeEnv(SONAR_QUBE_SERVER) {
                    sh "'${MAVEN_HOME}/bin/mvn' sonar:sonar"
                }
            }
        }

        stage('Deploy to Nexus') {
            steps {
                sh "'${MAVEN_HOME}/bin/mvn' deploy -DskipTests"
            }
        }

        stage('Deploy with Ansible') {
            steps {
                // Exécution du playbook Ansible pour le déploiement
                sh 'ansible-playbook -i ansible/inventory.ini ansible/deploy.yml'
            }
        }
    }

    post {
        always {
            junit '**/target/surefire-reports/*.xml'
            cleanWs()
        }
        success {
            echo 'Pipeline terminé avec succès !'
        }
        failure {
            echo 'Le pipeline a échoué.'
        }
    }
}

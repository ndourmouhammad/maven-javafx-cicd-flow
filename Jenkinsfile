pipeline {
    agent any

    environment {
        // Vérifie bien que le nom 'maven-3.9.12' est EXACTEMENT le même dans "Global Tool Configuration"
        MAVEN_HOME = tool 'maven-3.9.12'
        NEXUS_SETTINGS_ID = 'my-nexus-settings'
    }

    stages {
        stage('Checkout') {
            steps {
                // Remplace bien 'votre-pseudo/votre-repo' par tes vraies infos
                git branch: 'main',
                    credentialsId: 'github-ssh',
                    url: 'git@github.com:ndourmouhammad/maven-javafx-cicd-flow.git'
            }
        }

        stage('Build & Test') {
            steps {
                sh "${MAVEN_HOME}/bin/mvn clean package"
            }
        }

        stage('Analyse SonarQube') {
            steps {
                // 'SonarQube' doit correspondre au nom dans Jenkins > Configurer le système
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
                // Ajout de -v pour voir les erreurs détaillées d'Ansible en cas de souci
                sh "ansible-playbook -i ansible/inventory.ini ansible/deploy.yml -v"
            }
        }
    }

    post {
        always {
            // Utilisation du script block pour sécuriser le contexte JUnit
            script {
                try {
                    junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
                } catch (Exception e) {
                    echo "Erreur lors de la récupération des tests: ${e.message}"
                }
            }
            // On nettoie l'espace de travail seulement à la fin
            cleanWs()
        }
        success {
            echo '🚀 Pipeline terminé avec succès ! L\'application est déployée.'
        }
        failure {
            echo '❌ Le pipeline a échoué. Vérifiez les logs.'
        }
    }
}
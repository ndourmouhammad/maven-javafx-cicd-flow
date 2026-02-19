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
                // Jenkins récupère le code depuis ton repo
                git branch: 'main',
                    credentialsId: 'github-ssh',
                    url: 'https://github.com/ndourmouhammad/maven-javafx-cicd-flow.git'
            }
        }

        stage('Build & Test') {
            steps {
                // Compilation du projet Java
                sh "${MAVEN_HOME}/bin/mvn clean package"
            }
        }

        stage('Analyse SonarQube') {
            steps {
                // 'SonarQube' doit être configuré dans Jenkins > Système
                withSonarQubeEnv('SonarQube') {
                    sh "${MAVEN_HOME}/bin/mvn sonar:sonar"
                }
            }
        }

        stage('Deploy to Nexus') {
            steps {
                // Envoi du JAR vers ton Nexus local via Ngrok
                configFileProvider([configFile(fileId: "${NEXUS_SETTINGS_ID}", variable: 'MAVEN_SETTINGS')]) {
                    sh "${MAVEN_HOME}/bin/mvn deploy -s $MAVEN_SETTINGS -DskipTests"
                }
            }
        }

        stage('Deploy with Ansible') {
            steps {
                // Déploiement Docker sur l'EC2
                sh "ansible-playbook -i ansible/inventory.ini ansible/deploy.yml -v"
            }
        }
    }

    post {
        always {
            script {
                try {
                    // Archive les résultats de tests pour l'affichage graphique
                    junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
                } catch (e) {
                    echo "Avertissement : Aucun test trouvé ou erreur JUnit."
                }
            }
        }
        success {
            echo '🚀 Pipeline terminé avec succès ! L\'application est sur l\'EC2.'
        }
        failure {
            echo '❌ Le pipeline a échoué. Vérifiez les logs du stage rouge.'
        }
    }
}
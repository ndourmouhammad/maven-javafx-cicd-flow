pipeline {
    agent any

    environment {
        // Doit correspondre EXACTEMENT aux noms dans "Global Tool Configuration"
        MAVEN_HOME = tool 'maven-3.9.12'
        // Identifiant du fichier settings.xml créé dans "Managed Files"
        NEXUS_SETTINGS_ID = 'my-nexus-settings'
    }

    stages {
        stage('Checkout') {
            steps {
                // Utilisation de votre clé SSH configurée
                git branch: 'main',
                    credentialsId: 'github-ssh',
                    url: 'git@github.com:votre-pseudo/votre-repo.git'
            }
        }

        stage('Build & Test') {
            steps {
                sh "${MAVEN_HOME}/bin/mvn clean package"
            }
        }

        stage('Analyse SonarQube') {
            steps {
                // 'SonarQube' doit correspondre au nom dans Système > SonarQube installations
                withSonarQubeEnv('SonarQube') {
                    sh "${MAVEN_HOME}/bin/mvn sonar:sonar"
                }
            }
        }

        stage('Deploy to Nexus') {
            steps {
                // Utilise le settings.xml managé par Jenkins pour l'authentification Nexus
                configFileProvider([configFile(fileId: "${NEXUS_SETTINGS_ID}", variable: 'MAVEN_SETTINGS')]) {
                    sh "${MAVEN_HOME}/bin/mvn deploy -s $MAVEN_SETTINGS -DskipTests"
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    // On build l'image localement sur le serveur Jenkins
                    sh "docker build -t votre-dockerhub-ou-registry/votre-app:latest ."
                    // Si vous avez un registry, ajoutez le push ici
                }
            }
        }

        stage('Deploy with Ansible') {
            steps {
                // On précise l'inventaire et on lance le playbook
                sh "ansible-playbook -i ansible/inventory.ini ansible/deploy.yml"
            }
        }
    }

    post {
        always {
            // Récupère les rapports de tests pour l'affichage dans Jenkins
            junit '**/target/surefire-reports/*.xml'
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
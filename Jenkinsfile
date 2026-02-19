pipeline {
    agent any

    environment {
        // Doit correspondre au nom configuré dans "Global Tool Configuration"
        MAVEN_HOME = tool 'maven-3.9.12'
        // Identifiant du fichier settings.xml dans "Managed Files"
        NEXUS_SETTINGS_ID = 'my-nexus-settings'
    }

    stages {
        stage('Checkout') {
            steps {
                // Remplace par ton vrai dépôt
                git branch: 'main',
                    credentialsId: 'github-ssh',
                    url: 'git@github.com:ton-pseudo/ton-repo.git'
            }
        }

        stage('Build & Test') {
            steps {
                sh "${MAVEN_HOME}/bin/mvn clean package"
            }
        }

        stage('Analyse SonarQube') {
            steps {
                // 'SonarQube' doit correspondre au nom dans Configurer le système
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
                // -v permet d'avoir plus de détails en cas d'erreur de connexion
                sh "ansible-playbook -i ansible/inventory.ini ansible/deploy.yml -v"
            }
        }
    }

    post {
        always {
            // On réalloue un contexte node pour éviter l'erreur MissingContextVariableException
            node {
                script {
                    try {
                        junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
                    } catch (Exception e) {
                        echo "Avertissement JUnit : ${e.message}"
                    }
                    // Nettoyage obligatoire dans un contexte node
                    cleanWs()
                }
            }
        }
        success {
            echo '🚀 Pipeline terminé avec succès !'
        }
        failure {
            echo '❌ Le pipeline a échoué. Vérifiez les logs des stages précédents.'
        }
    }
}

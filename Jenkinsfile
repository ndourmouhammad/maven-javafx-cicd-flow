pipeline {
    agent any

    environment {
        // Vérifie dans Jenkins > Admin > Tools que le nom est exactement celui-ci
        MAVEN_HOME = tool 'maven-3.9.12'
        // Vérifie dans Jenkins > Admin > Managed Files que l'ID est celui-ci
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
                // Le nom 'SonarQube' doit exister dans la config système de Jenkins
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
                // Ansible doit être installé sur le serveur où tourne Jenkins
                sh "ansible-playbook -i ansible/inventory.ini ansible/deploy.yml -v"
            }
        }
    }

    post {
        always {
            script {
                // On utilise une gestion d'erreur simple pour éviter que le nettoyage ne bloque tout
                try {
                    junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
                } catch (e) {
                    echo "Pas de rapports de tests trouvés ou erreur JUnit."
                }
            }
        }
        success {
            echo '🚀 Pipeline terminé avec succès !'
        }
        failure {
            echo '❌ Le pipeline a échoué. Regardez les logs du stage en rouge.'
        }
    }
}
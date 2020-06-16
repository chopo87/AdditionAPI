pipeline {
    agent any
    tools{
        maven 'Maven 3.3.9'
        jdk 'JDK 1.8'
    }

    stages {
        
        stage('Cleanup') {
            steps {
                deleteDir()
            }
        }
        
        stage('Sources') {
            steps {
              git url: 'git@github.com:chopo87/AdditionAPI.git', credentialsId: 'Deontics-Test-GitHub-Key-Aitzol', branch: 'master'
            }
        }
        
        stage('Build & Unit Test') {
          steps{
            sh "mvn package"
            sh 'ls -ltr'
            sh 'ls -ltr target'
          }
        }
    }
}

@Library('jenkins-shared-libraries') _

def SERVER_ID  = 'carlspring-oss-snapshots'
def DEPLOY_SERVER_URL = 'https://repo.carlspring.org/content/repositories/carlspring-oss-snapshots/'
def PR_SERVER_URL = 'https://repo.carlspring.org/content/repositories/carlspring-oss-pull-requests/'

// Notification settings for "master" and "branch/pr"
def notifyMaster = [notifyAdmins: true, recipients: [culprits(), requestor()]]
def notifyBranch = [recipients: [brokenTestsSuspects(), requestor()], notifyByChat: false]

pipeline {
    agent {
        node {
            label 'alpine:jdk8-mvn-3.5'
            customWorkspace workspace().getUniqueWorkspacePath()
        }
    }
    parameters {
        booleanParam(defaultValue: true, description: 'Send email notification?', name: 'NOTIFY_EMAIL')
    }
    options {
        timeout(time: 1, unit: 'HOURS')
        disableConcurrentBuilds()
    }
    stages {
        stage('Node')
        {
            steps {
                nodeInfo("mvn")
            }
        }
        stage('Build')
        {
            steps {
                withMavenPlus(mavenLocalRepo: workspace().getM2LocalRepoPath(), mavenSettingsConfig: 'a5452263-40e5-4d71-a5aa-4fc94a0e6833')
                {
                    sh "mvn -U clean install -Dmaven.test.failure.ignore=true"
                }
            }
        }
        stage('Code Analysis') {
            steps {
                withMaven(maven: 'maven-3.5', mavenSettingsConfig: 'a5452263-40e5-4d71-a5aa-4fc94a0e6833')
                {
                    script {
                        if(env.BRANCH_NAME == 'master') {
                            withSonarQubeEnv('sonar') {
                                // requires SonarQube Scanner for Maven 3.2+
                                sh "mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.4.0.905:sonar"
                            }
                        }
                        else {
                            if(env.BRANCH_NAME.startsWith("PR-"))
                            {
                                withSonarQubeEnv('sonar') {
                                    def PR_NUMBER = env.CHANGE_ID
                                    echo "Triggering sonar analysis in comment-only mode for PR: ${PR_NUMBER}."
                                    sh "mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.4.0.905:sonar" +
                                       " -Psonar-github" +
                                       " -Dsonar.github.repository=${REPO_NAME}" +
                                       " -Dsonar.github.pullRequest=${PR_NUMBER}"
                                }
                            }
                            else
                            {
                                echo "This step is skipped for branches other than master or PR-*"
                            }
                        }
                    }
                }
            }
        }
        stage('Deploy') {
            when {
                expression { (currentBuild.result == null || currentBuild.result == 'SUCCESS') }
            }
            steps {
                script {
                    withMavenPlus(mavenLocalRepo: workspace().getM2LocalRepoPath(), mavenSettingsConfig: 'a5452263-40e5-4d71-a5aa-4fc94a0e6833')
                    {

                        def SERVER_URL;

                        if (BRANCH_NAME == 'master') {
                            echo "Deploying master..."
                            SERVER_URL = DEPLOY_SERVER_URL;
                        } else {
                            echo "Deploying branch/PR"

                            def pom = readMavenPom file: 'pom.xml'
	                        def VERSION_ID = pom.version

                          	def gitBranch = sh(returnStdout: true, script: 'git rev-parse --abbrev-ref HEAD').trim()

                            SERVER_URL = PR_SERVER_URL;
                            VERSION_ID = VERSION_ID.replaceAll("-SNAPSHOT", "") + "-${GIT_BRANCH}";

                            sh "mvn versions:set -DnewVersion=${VERSION_ID}-SNAPSHOT"
                            sh "mvn versions:commit"
                        }

                        sh "mvn package deploy:deploy" +
                           " -Drat.ignoreErrors=true" +
                           " -Dmaven.test.skip=true" +
                           " -DaltDeploymentRepository=${SERVER_ID}::default::${SERVER_URL}"
                    }
                }
            }
        }
    }
    post {
        failure {
            script {
                if(params.NOTIFY_EMAIL) {
                    notifyFailed((BRANCH_NAME == "master") ? notifyMaster : notifyBranch)
                }
            }
        }
        unstable {
            script {
                if(params.NOTIFY_EMAIL) {
                    notifyUnstable((BRANCH_NAME == "master") ? notifyMaster : notifyBranch)
                }
            }
        }
        fixed {
            script {
                if(params.NOTIFY_EMAIL) {
                    notifyFixed((BRANCH_NAME == "master") ? notifyMaster : notifyBranch)
                }
            }
        }
        always {
            // (fallback) record test results even if withMaven should have done that already.
            junit '**/target/*-reports/*.xml'
        }
        cleanup {
            script {
                workspace().clean()
            }
        }
    }
}

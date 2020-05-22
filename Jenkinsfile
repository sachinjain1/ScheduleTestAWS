#!groovy

/*********************************************************************
***** Description :: This template is used for Pass API gateway service *****
***** Author *****:: Sachin jain *********
***** Date ******:: 22/05/2020*******
***** Revision *****:: 1.0*****
***************
*********************************************************************/

import hudson.model.*
import hudson.EnvVars
import groovy.json.JsonSlurperClassic
import groovy.json.JsonBuilder
import groovy.json.JsonOutput 
import java.net.URL
import groovy.transform.Field

properties([buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5'))])

imageName = "scheduler-backup-aws"
summaryTemp = "Docker Image build or push failed: (${env.JOB_NAME}) (${env.BUILD_URL})"
MavenPlugin = "3.2"
TOOLMAVEN = "MAVEN"
imageRegistry = "https://registry.hub.docker.com"
currentDate = new Date().format( 'yyyy.MM.dd' )
BRANCH_NAME = ""
NAMESPACE = ""
KUBECONFIGPATH = ""
DBCONFIGMAP = ""
SCHEDULE = ""
projectName="scheduler-backup-aws"

def funCodeCheckout() {
deleteDir()
checkout scm
}
def funUnitTest() {
echo "Dummy unit test"
}

def funPreBuild() {
    sh """
    cd $WORKSPACE
    git name-rev --name-only HEAD > GIT_BRANCH
    cat GIT_BRANCH  | cut -f3 -d "/" > branch-name
     """
    BRANCH_NAME = readFile('branch-name').trim()
    
    if (BRANCH_NAME == 'master') 
					{
		  	KUBECONFIGPATH = "/var/lib/jenkins/.kube/config"
          		DEP_ENV = "prod"
			IMAGEVERSION = "${currentDate}.${BUILD_NUMBER}.${DEP_ENV}"
			SCHEDULE = "5 * * * *"
			}
	
    if (BRANCH_NAME == 'dev') {
		        KUBECONFIGPATH = "/home/asset/kubernetes-config"
		        DEP_ENV = "uat"
		        IMAGEVERSION = "${currentDate}.${BUILD_NUMBER}.${DEP_ENV}"
		    	SCHEDULE = "47 12 * * *"
	}
}



def funCodeCompile(){  
    sh "echo $KUBECONFIGPATH"
    sh "echo $IMAGEVERSION"
	sh "echo $SCHEDULE"	
withMaven(maven: 'maven3',jdk: 'JDK8') {
      sh "mvn clean install -f pom.xml -Dmaven.test.skip=true"
    }
}

def funCheckmarxTest(){
step([$class: 'CxScanBuilder', comment: '', credentialsId: '', excludeFolders: 'node_modules,.gradle', excludeOpenSourceFolders: '', exclusionsSetting: 'job', failBuildOnNewResults: false, failBuildOnNewSeverity: 'HIGH', filterPattern: '', fullScanCycle: 7, fullScansScheduled: true, generatePdfReport: true, groupId: '17a9ed39-73d6-46ec-a32e-83d56ee586f4', includeOpenSourceFolders: '', incremental: true, osaArchiveIncludePatterns: '*.zip, .war, .ear, *.tgz', osaInstallBeforeScan: false, password: '{AQAAABAAAAAQ5iBF15NPiMjfxG6w+CBqtJbiArtcWm5qPJefRDNfJtk=}', preset: '42', projectName: projectName, serverUrl: 'https://cx.mbusa.com/', sourceEncoding: '1', username: '', vulnerabilityThresholdResult: 'FAILURE', waitForResultsEnabled: true])
}

def funDockerbuild() {
	sh "cd $WORKSPACE && sh /var/lib/jenkins/login.sh && docker build -t $imageName . && docker tag $imageName $imageRegistry/$imageName:$IMAGEVERSION && docker push $imageRegistry/$imageName:$IMAGEVERSION"

}

 def funhelmdeploy() 
     {
         sh """#!/bin/bash
         pwd
         cd $WORKSPACE
         sed -i "s/IMAGEVERSION/$IMAGEVERSION/g" scheduler.yaml
    	 sed -i "s/SCHEDULE/$SCHEDULE/g" scheduler.yaml
         export KUBECONFIG=$KUBECONFIGPATH
         kubectl apply -f scheduler.yaml
         """
     }


node() {
stage ("Checkout") {funCodeCheckout()}
stage ("PreBuild") {funPreBuild()}
//stage("Run Checkmarx Test"){funCheckmarxTest()}
stage ("Build") {funCodeCompile()}
stage ("Build Docker Image and Push") {funDockerbuild()}
stage ("Helm Deploy") {funhelmdeploy()}
}





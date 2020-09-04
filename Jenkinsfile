
node {
    stage('Init setup'){
		catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE'){
	    	sh """
                docker network rm sonarqube-tier
                docker volume create --name sonarqube_data
         	"""
	  	}
    }
    stage('pre-build setup'){
		catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE'){
	    	sh """
            	docker-compose -f Sonarqube/sonar.yml up -d
            	docker-compose -f Anchore-Engine/docker-compose.yaml up -d
         	"""
	  	}
    } 
    stage('SCM') {
    checkout poll: false, scm: [$class: 'GitSCM', branches: [[name: 'master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/boriphuth/sample.git']]]
    }
    stage('SAST'){
		catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE'){
				withSonarQubeEnv('sonarqube'){
					dir("backend"){
						sh "mvn clean package sonar:sonar"
					}
				}
				
				timeout(time: 1, unit: 'HOURS'){   
				def qg = waitForQualityGate() 
				if (qg.status != 'OK') {     
						error "Pipeline aborted due to quality gate failure: ${qg.status}"    
					}	
				}
    	}
	}
}

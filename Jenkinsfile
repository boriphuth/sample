
node {
    stage('Init setup'){
		catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE'){
	    	sh """
                mkdir -p volumes/conf
                mkdir -p volumes/extensions
                mkdir -p volumes/data
                chmod -R 777 ./volumes 
                
                docker run --rm \
                -v $PWD/volumes/conf:/opt/sonarqube/conf \
                -v $PWD/volumes/extensions:/opt/sonarqube/extensions \
                -v $PWD/volumes/data:/opt/sonarqube/data \
                sonarqube:community-beta --init
                
                find ./volumes -type d -exec sudo chmod 777 {} + 
                sudo cp sonar.properties ./volumes/conf/
         	"""
	  	}
    }
    stage('pre-build setup'){
		catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE'){
	    	sh """
            	docker-compose -f Sonarqube/sonar.yml up -d
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

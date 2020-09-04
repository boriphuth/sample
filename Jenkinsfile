
node {
    // stage('Init setup'){
	// 	catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE'){
	//     	sh """
    //             mkdir -p volumes/sonarqube_conf
    //             mkdir -p volumes/sonarqube_data
    //             mkdir -p volumes/sonarqube_extensions
    //             mkdir -p volumes/sonarqube_bundled-plugins
    //             sudo chmod -R 777 ./volumes 
                
    //             docker run --rm \
    //             -v $PWD/volumes/sonarqube_conf:/opt/sonarqube/conf \
    //             -v $PWD/volumes/sonarqube_data:/opt/sonarqube/data \
    //             -v $PWD/volumes/sonarqube_extensions:/opt/sonarqube/extensions \
    //             -v $PWD/volumes/sonarqube_bundled-plugins:/opt/sonarqube/lib/bundled-plugins \
    //             sonarqube:7.7-community --init
                
    //             find ./volumes -type d -exec sudo chmod 777 {} + 
    //             sudo cp sonar.properties ./volumes/conf/
    //      	"""
	//   	}
    // }
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

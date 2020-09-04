
node {
    stage('Init Sonarqube'){
		catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE'){
	    	sh """
                sysctl -w vm.max_map_count=262144
                sysctl -w fs.file-max=65536
                ulimit -n 65536
                ulimit -u 4096
                docker volume create --name sonarqube_data
                docker volume create --name sonarqube_extensions
                docker volume create --name sonarqube_logs
                docker run --rm \
                -p 9000:9000 \
                -v sonarqube_extensions:/opt/sonarqube/extensions \
                sonarqube:8.4-community
         	"""
	  	}
    }
    stage('pre-build setup'){
		catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE'){
	    	sh """
                docker run -d \
                -p 9000:9000 \
                -v sonarqube_extensions:/opt/sonarqube/extensions \
                sonarqube:8.4-community
                sleep 15 # wait for db to come up
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

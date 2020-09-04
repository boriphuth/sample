
node {
    stage('Init Sonarqube'){
		catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE'){
	    	sh """
                docker volume create --name sonarqube_data
                docker volume create --name sonarqube_extensions
                docker volume create --name sonarqube_logs
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
         	"""
            stimeout(120) {
                waitUntil {
                    try {
                        sh 'wget -q http://192.168.34.16:9000 -O /dev/null'
                        return true
                    } catch (exception) {
                        return false
                    }
                }
            }
	  	}
    } 
    // stage('pre-build setup'){
	// 	catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE'){
	//     	sh """
    //             docker-compose -f Sonarqube/sonar.yml up -d
    //      	"""
	//   	}
    // } 
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
            
            sleep(60)

			timeout(5) {
                def qg = waitForQualityGate() 
                if (qg.status != 'OK') {     
                            error "Pipeline aborted due to quality gate failure: ${qg.status}"    
                }	
		    }
    	}
	}
    stage('Clean up'){
		catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE'){
        	sh """
				rm -r sample || true
				mkdir -p reports/trufflehog
				mkdir -p reports/snyk
				mkdir -p reports/Anchore-Engine
				mkdir -p reports/OWASP
				mkdir -p reports/Inspec
            	mv trufflehog reports/trufflehog || true
				mv *.json *.html reports/snyk || true
				cp -r /var/lib/jenkins/jobs/${JOB_NAME}/builds/${BUILD_NUMBER}/archive/Anchore*/*.json ./reports/Anchore-Engine ||  true
				mv inspec_results reports/Inspec || true
            """
			//cp Archerysec-ZeD/owasp_report reports/OWASP/ || ture	    
			sh """
				docker system prune -f
				docker rm -vf \$(docker ps -a -q)
			"""
	  	}
    }
}

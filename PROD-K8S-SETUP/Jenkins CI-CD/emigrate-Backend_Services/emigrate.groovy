import org.apache.commons.lang.StringUtils

def call (String stageName) {

    if ("${stageName}" == 'Checkout') {
	   git branch: '${ENVIRONMENT}',
     credentialsId: '692bbda9-3741-4662-8371-92df1ccfcac3',
     url: 'https://apps.trigyn.com/gitlab/emigrate/emigratebackend.git'
	} 

    else if ("${stageName}" == 'Build' ) {
	
     sh "mvn -U -s Maven/settings.xml -f ${SERVICE_NAME}/pom.xml clean install -DskipTests"
       //sh "mvn -f ${SERVICE_NAME}/pom.xml clean install -DskipTests"

			   
    }
	
    else if ("${stageName}" == 'codeQualityAnalysis' ) {
   
     withCredentials([string(credentialsId: 'emigrate-sonar-qube-token', variable: 'SONAR_TOKEN')]) {
     withSonarQubeEnv('Trigyn-sonar-qube-instance') {
                     //   sh "mvn -f ${ServiceName}/pom.xml clean install -DskipTests sonar:sonar -Dsonar.login=${SONAR_TOKEN}"
          sh '''            
            mvn -U -s Maven/settings.xml -f ${SERVICE_NAME}/pom.xml sonar:sonar \
            -Dsonar.login=${SONAR_TOKEN}
			
            '''
                    }
                }
   
   }
   
   else if ("${stageName}" == 'archieveArtifacts' ) {
   
     archiveArtifacts artifacts: 'SvrJsbJarFdr/*.jar', fingerprint: true   
   
   }
   
   else if ("${stageName}" == 'pushToNexus' ) {
   
     nexusArtifactUploader artifacts: [[artifactId: '${SERVICE_NAME}', classifier: '${BUILD_NUMBER}', file: '${JENKINS_HOME}/jobs/${JOB_NAME}/builds/${BUILD_NUMBER}/archive/SvrJsbJarFdr/${SERVICE_NAME}-0.0.1-SNAPSHOT.jar', type: 'jar']], credentialsId: '70f0d661-f381-460a-b18a-cffaa01ad2bc', groupId: 'com.gov.emigrate.master', nexusUrl: '${NEXUS_URL}', nexusVersion: 'nexus3', protocol: 'http', repository: 'emigrate-services-snapshot-repo', version: '0.0.1-SNAPSHOT'    

   }
   
   else if ("${stageName}" == 'dockerImagePushToNexus' ) {
   
       def serviceName = env.SERVICE_NAME
       def lowerServiceName = serviceName.toLowerCase()
                         
                      //   println "Original Service Name: ${SERVICE_NAME}"
                      //  println "Lowercase Service Name: ${lowerServiceName}"
					  
					  if ("${SERVICE_NAME}" == 'GatewayService') {
                        sh    "cp ${SERVICE_NAME}/src/main/resources/application_UAT.properties ${SERVICE_NAME}/"
						
                        }  

                        sh """
                           echo "Creating an Docker Image"
                           cp services_UAT.properties ${SERVICE_NAME}/
                          #cp SvrJsbJarFdr/*.jar ${SERVICE_NAME}/
                           cd ${SERVICE_NAME}
                           cp target/*.jar .
                          #mv services_K8S.properties  services_UAT.properties
                           docker build -f ./Dockerfile  -t ${HTTP_PROXY_NEXUS_HOST}/${lowerServiceName}:${BUILD_NUMBER} --build-arg ENVIRONMENT=UAT ./ || exit 1;
                           docker push ${HTTP_PROXY_NEXUS_HOST}/${lowerServiceName}:${BUILD_NUMBER}
                           docker rmi -f ${HTTP_PROXY_NEXUS_HOST}/${lowerServiceName}:${BUILD_NUMBER}
                           sleep 3
                      
                        """
   
   }
   else if ("${stageName}" == 'deploymentToKuberentes' ) {
   
       def serviceName = env.SERVICE_NAME
       def lowerServiceName = serviceName.toLowerCase()
                        
                //   withCredentials([file(credentialsId: 'emigrate_kube_configfile', variable: 'KUBECONFIG')]) {
                sh """
                cd k8s/dev
                sed -i 's|'${lowerServiceName}:latest'|'${lowerServiceName}:${BUILD_NUMBER}'|g' ./${lowerServiceName}-deployment.yml
                #kubectl apply -f namespace.yml
                kubectl apply -f ${lowerServiceName}-deployment.yml
                #kubectl get pod -n emigrate-dev-ns  

                """
               //    }
   
   }


   else if ("${stageName}" == 'monitoringDeployment' ) {
        
        def serviceName = env.SERVICE_NAME
       def lowerServiceName = serviceName.toLowerCase()
                
            // Wait for a few seconds to allow pods to start
            sleep 15
              sh 'kubectl get pod -n emigrate-dev-ns' 
            // Check pod status
            def podStatus = sh(script: "kubectl get pods -n emigrate-dev-ns -o json", returnStdout: true).trim()

            // Parse JSON response using jsonSlurper
            def podStatusObject = new groovy.json.JsonSlurper().parseText(podStatus)

            // Check the status of each pod
            boolean allRunning = true
            podStatusObject.items.each { pod ->
                def podName = pod.metadata.name
                def podPhase = pod.status.phase
                
                if (podPhase != "Running") {
                    allRunning = false
                   // echo "Pod ${podName} is not running. Phase: ${podPhase}"

                    if (podName.contains(lowerServiceName)) {
                               // echo "Pod ${podName} contains the service name ${env.SERVICE_NAME}. Failing the build."
                                currentBuild.result = 'FAILURE'
                                error("Build failure: Pod '${podName}' is not running in service '${env.SERVICE_NAME}'. Please check the pod's status and logs for more details.")

                            }
                }
            }

      
            
           
        }

   

}

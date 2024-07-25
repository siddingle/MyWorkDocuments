def checkoutScm = {
        git branch: env.ENVIRONMENT,
        credentialsId: '692bbda9-3741-4662-8371-92df1ccfcac3',
        url: 'https://apps.trigyn.com/gitlab/emigrate/emigratebackend.git'
}

def build() {
	
     sh "mvn -f ${SERVICE_NAME}/pom.xml clean install -DskipTests"
			   
    }
	
def codeQualityAnalysis() {
   
     withCredentials([string(credentialsId: 'emigrate-sonar-qube-token', variable: 'SONAR_TOKEN')]) {
     withSonarQubeEnv('Trigyn-sonar-qube-instance') {
                     //   sh "mvn -f ${ServiceName}/pom.xml clean install -DskipTests sonar:sonar -Dsonar.login=${SONAR_TOKEN}"
          sh '''            
            mvn -f ${SERVICE_NAME}/pom.xml sonar:sonar \
            -Dsonar.login=${SONAR_TOKEN}
			
            '''
                    }
         }  
   }
   
def archieveArtifacts() {
   
     archiveArtifacts artifacts: 'SvrJsbJarFdr/*.jar', fingerprint: true   
   
   }
   
def pushToNexus() {
   
     nexusArtifactUploader artifacts: [[artifactId: '${SERVICE_NAME}', classifier: '${BUILD_NUMBER}', file: '${JENKINS_HOME}/jobs/${JOB_NAME}/builds/${BUILD_NUMBER}/archive/SvrJsbJarFdr/${SERVICE_NAME}-0.0.1-SNAPSHOT.jar', type: 'jar']], credentialsId: '70f0d661-f381-460a-b18a-cffaa01ad2bc', groupId: 'com.gov.emigrate.master', nexusUrl: '${NEXUS_URL}', nexusVersion: 'nexus3', protocol: 'http', repository: 'emigrate-services-snapshot-repo', version: '0.0.1-SNAPSHOT'    

   }
   
def dockerImagePushToNexus() {
   
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
                           docker build -f ./Dockerfile  -t ${lowerServiceName}:${BUILD_NUMBER} --build-arg ENVIRONMENT=UAT ./ || exit 1;
                           docker tag ${lowerServiceName}:${BUILD_NUMBER} ${HTTP_PROXY_NEXUS_HOST}/${lowerServiceName}:${BUILD_NUMBER}
                           docker push ${HTTP_PROXY_NEXUS_HOST}/${lowerServiceName}:${BUILD_NUMBER}
                           docker rmi -f ${HTTP_PROXY_NEXUS_HOST}/${lowerServiceName}:${BUILD_NUMBER}
                           docker rmi -f ${lowerServiceName}:${BUILD_NUMBER}
                          
                      
                        """
   
   }
   
def deploymentToKuberentes() {
   
       def serviceName = env.SERVICE_NAME
       def lowerServiceName = serviceName.toLowerCase()
                        
                   withCredentials([file(credentialsId: 'emigrate_kube_configfile', variable: 'KUBECONFIG')]) {
                sh """
                kubectl get nodes -o wide
                kubectl get pod -A
                
                #kubectl apply -f ${lowerServiceName}-deployment.yml
                """
                   }
   
   }
   



import org.apache.commons.lang.StringUtils

def call (String stageName) {

    if ("${stageName}" == 'checkoutSCM') {
           git branch: 'env.ENVIRONMENT',
        credentialsId: 'da6486cc-b296-4e1d-9301-7d1bad450eef',
        url: 'https://apps.trigyn.com/gitlab/emigrate/emigratebackend.git'
        }

    else if ("${stageName}" == 'checkoutCdn' ) {

          dir('emigratecdn') {
           git branch: 'master',
           credentialsId: 'da6486cc-b296-4e1d-9301-7d1bad450eef',
           url: 'https://apps.trigyn.com/gitlab/emigrate/emigratecdn.git'
                }

    }

    else if ("${stageName}" == 'addPdfs' ) {

    // sh 'sudo cp -R emigratecdn/assets/pdfs src/assets/'
    
       sh "cp -R emigratecdn/assets/pdfs dist/e-migrate/assets/"
       sh "chmod -R 777 dist/e-migrate/assets/"

    }

    else if ("${stageName}" == 'build' ) {

        nodejs('NPM') {
     sh '''
            #npm install --force --offline --cache ~/.npm
            npm install --force
            npm run package
            npm run build:stqc
           '''
        }
    }

    else if ("${stageName}" == 'createZip') {
       
        def environment = env.ENVIRONMENT
               def lowerenv = environment.toLowerCase()

        sh """
               # filename=e-migrate-${ENVIRONMENT}.zip
                rm -rf *.zip 
                cd dist
                zip  ../e-migrate-${lowerenv}.zip -rq ./e-migrate/ 
                cd ..
                
                """
    }




   else if ("${stageName}" == 'dockerImagePushToNexus' ) {

               def environment = env.ENVIRONMENT
               def lowerenv = environment.toLowerCase()
               
                sh """
                echo "${ENVIRONMENT}"
                echo ${lowerenv}
                 docker build -f ./Dockerfile -t ${HTTP_PROXY_NEXUS_HOST}/emigrate-ui-${lowerenv}:${BUILD_NUMBER} .
                 docker push ${HTTP_PROXY_NEXUS_HOST}/emigrate-ui-${lowerenv}:${BUILD_NUMBER}
                 docker rmi -f ${HTTP_PROXY_NEXUS_HOST}/emigrate-ui-${lowerenv}:${BUILD_NUMBER}
                """

   }


   else if ("${stageName}" == 'deploymentToKuberentes' ) {

                  def environment = env.ENVIRONMENT
                  def lowerenv = environment.toLowerCase()


              //  withCredentials([file(credentialsId: 'emigrate_kube_configfile', variable: 'KUBECONFIG')]) {

                    sh """
                    kubectl get nodes
                    cd k8s/dev

                    sed -i 's|'emigrate-ui:latest'|'emigrate-ui-${lowerenv}:${BUILD_NUMBER}'|g' ./emigrate-ui-deployment.yaml

                    kubectl apply -f emigrate-ui-deployment.yaml

                    """
                //   }

   }


   else if ("${stageName}" == 'monitoringDeployment' ) {
                
            // Wait for a few seconds to allow pods to start
            sleep 30
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

                    if (podName.contains(env.SERVICE_NAME)) {
                              //  echo "Pod ${podName} contains the service name ${env.SERVICE_NAME}. Failing the build."
                                currentBuild.result = 'FAILURE'
                                error("Build failure: Pod '${podName}' is not running in service '${env.SERVICE_NAME}'. Please check the pod's status and logs for more details.")


                            }
                }
            }

      
            
           
        }



}

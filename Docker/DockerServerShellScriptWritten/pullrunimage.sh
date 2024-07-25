#!/bin/sh

service="$1"
buildn="$2"

HOSTPATH="/workspace/logConfig"
MOUNTPATH="/emigrate/logConfig"
HOSTPATH1="/workspace/emigratedocs/"
MOUNTPATH1="/home/migration"

#if [ $buildn -gt 0 ]
#then
        echo "Docker Nexus login for $service-$buildn  :- $(date +%T)"
        pwd

        docker login http://192.168.150.8:9081/nexus/repository/emigrate-docker-hosted-repo/ -u admin -p Emigrate#007

        echo "Stoped Docker Container ${service}"
        docker stop ${service}
        echo "Removed Docker Container ${service}"
        docker rm ${service}
        echo "Deleted Existing ${service} Image"
        docker rmi -f 192.168.150.8:9081/nexus/${service}:latest
        echo "Docker Image Pulled from Nexus Repository"
        docker pull 192.168.150.8:9081/nexus/${service}:${buildn}
        echo "Docker Image tag with a 'latest'"
        docker tag 192.168.150.8:9081/nexus/${service}:${buildn} 192.168.150.8:9081/nexus/${service}:latest

        docker rmi -f 192.168.150.8:9081/nexus/${service}:${buildn}

        #echo "Image Pulled from nexus :- $(date +%T)"
       
       	echo "Running Docker Images"
    if [ ${service} = administrationservice ]; then
        docker run -d --name ${service} -v ${HOSTPATH}:${MOUNTPATH} -v ${HOSTPATH1}:${MOUNTPATH1}  -p 8081:8081 192.168.150.8:9081/nexus/${service}:latest
        echo "Docker Image for ${service}:${buildn} is running....  :- $(date +%T)"
	
    elif [ ${service} = gatewayservice ]; then
        docker run -d --name ${service} -v ${HOSTPATH}:${MOUNTPATH} -v ${HOSTPATH1}:${MOUNTPATH1}   -p 8080:8080 192.168.150.8:9081/nexus/${service}:latest
	echo "Docker Image for ${service}:${buildn} is running....  :- $(date +%T)"	

    elif [ ${service} = ereportservice ]; then
        docker run -d --name ${service} -v ${HOSTPATH}:${MOUNTPATH} -v ${HOSTPATH1}:${MOUNTPATH1}   -p 8099:8099 192.168.150.8:9081/nexus/${service}:latest
        echo "Docker Image for ${service}:${buildn} is running....  :- $(date +%T)"

elif [ ${service} = paymentservice ]; then
        docker run -d --name ${service} -v ${HOSTPATH}:${MOUNTPATH} -v ${HOSTPATH1}:${MOUNTPATH1}   -p 8094:8094 192.168.150.8:9081/nexus/${service}:latest
        echo "Docker Image for ${service}:${buildn} is running....  :- $(date +%T)"

elif [ ${service} = reportservice ]; then
        docker run -d --name ${service} -v ${HOSTPATH}:${MOUNTPATH} -v ${HOSTPATH1}:${MOUNTPATH1}   -p 8085:8085 192.168.150.8:9081/nexus/${service}:latest
        echo "Docker Image for ${service}:${buildn} is running....  :- $(date +%T)"

elif [ ${service} = notificationservice ]; then
        docker run -d --name ${service} -v ${HOSTPATH}:${MOUNTPATH}  -v ${HOSTPATH1}:${MOUNTPATH1}  -p 8086:8086 192.168.150.8:9081/nexus/${service}:latest
        echo "Docker Image for ${service}:${buildn} is running....  :- $(date +%T)"

elif [ ${service} = workflowservice ]; then
        docker run -d --name ${service} -v ${HOSTPATH}:${MOUNTPATH}  -v ${HOSTPATH1}:${MOUNTPATH1}  -p 8088:8088 192.168.150.8:9081/nexus/${service}:latest
        echo "Docker Image for ${service}:${buildn} is running....  :- $(date +%T)"

elif [ ${service} = securityservice ]; then
        docker run -d --name ${service} -v ${HOSTPATH}:${MOUNTPATH}  -v ${HOSTPATH1}:${MOUNTPATH1} -p 8087:8087 192.168.150.8:9081/nexus/${service}:latest
        echo "Docker Image for ${service}:${buildn} is running....  :- $(date +%T)"

elif [ ${service} = recruitingagentservice ]; then
        docker run -d --name ${service} -v ${HOSTPATH}:${MOUNTPATH}  -v ${HOSTPATH1}:${MOUNTPATH1} -p 8082:8082 192.168.150.8:9081/nexus/${service}:latest
        echo "Docker Image for ${service}:${buildn} is running....  :- $(date +%T)"

elif [ ${service} = grievanceservice ]; then
        docker run -d --name ${service} -v ${HOSTPATH}:${MOUNTPATH}  -v ${HOSTPATH1}:${MOUNTPATH1}  -p 8092:8092 192.168.150.8:9081/nexus/${service}:latest
        echo "Docker Image for ${service}:${buildn} is running....  :- $(date +%T)"

elif [ ${service} = foreignrecruiterservice ]; then
        docker run -d --name ${service} -v ${HOSTPATH}:${MOUNTPATH}  -v ${HOSTPATH1}:${MOUNTPATH1}  -p 8083:8083 192.168.150.8:9081/nexus/${service}:latest
        echo "Docker Image for ${service}:${buildn} is running....  :- $(date +%T)"

elif [ ${service} = emigrantservice ]; then
        docker run -d --name ${service} -v ${HOSTPATH}:${MOUNTPATH}  -v ${HOSTPATH1}:${MOUNTPATH1} -p 8084:8084 192.168.150.8:9081/nexus/${service}:latest
        echo "Docker Image for ${service}:${buildn} is running....  :- $(date +%T)"

elif [ ${service} = authenticationservice ]; then
        docker run -d --name ${service} -v ${HOSTPATH}:${MOUNTPATH}  -v ${HOSTPATH1}:${MOUNTPATH1}  -p 8181:8181 192.168.150.8:9081/nexus/${service}:latest
        echo "Docker Image for ${service}:${buildn} is running....  :- $(date +%T)"

elif [ ${service} = documentserverservice ]; then
        docker run -d --name ${service} -v ${HOSTPATH}:${MOUNTPATH}  -v ${HOSTPATH1}:${MOUNTPATH1}  -p 8093:8093 192.168.150.8:9081/nexus/${service}:latest
        echo "Docker Image for ${service}:${buildn} is running....  :- $(date +%T)"

elif [ ${service} = insuranceservice ]; then
        docker run -d --name ${service} -v ${HOSTPATH}:${MOUNTPATH}  -v ${HOSTPATH1}:${MOUNTPATH1}  -p 8096:8096 192.168.150.8:9081/nexus/${service}:latest
        echo "Docker Image for ${service}:${buildn} is running....  :- $(date +%T)"
    
    fi

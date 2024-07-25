#/bin/bash

service=emigrate-ui

echo "Stoped Docker Container ${service}"
        docker stop ${service}
        echo "Removed Docker Container ${service}"
        docker rm ${service}

docker build -t ${service} .
docker run -dit --name ${service} -p 18080:80 ${service}

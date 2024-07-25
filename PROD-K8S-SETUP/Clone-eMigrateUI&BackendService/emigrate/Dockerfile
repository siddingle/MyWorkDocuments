#FROM httpd:2.4
#FROM registry.access.redhat.com/ubi9/httpd-24:1-321.1716477798
#FROM 192.168.70.16:5071/ubi9/httpd-24:1-321.1716477798
FROM nexuspoc.mea-emigrate.com/ubi9/httpd-24:1-321.1716477798

ARG BUILD_ID            xx
#ENV SERVICE_NAME        dist/e-migrate
ENV SERVICE_NAME         e-migrate-dev-k8-poc-v2.zip 
ENV VERSION             0.0.1-SNAPSHOT
ENV SERVICE_PORT        8080
ARG ENVIRONMENT         QA
ENV WORK_DIR            /var/www/newbuild
ENV BASE_DIR            /var/www/html

RUN echo "BUILD_ID":${BUILD_ID}
RUN echo "SERVICE_NAME":${SERVICE_NAME}
RUN echo "VERSION":${VERSION}
RUN echo "SERVICE_PORT":${SERVICE_PORT}
RUN echo "ENVIRONMENT":${ENVIRONMENT}
RUN echo "WORK_DIR":${WORK_DIR}

# set the work dir in container
WORKDIR ${WORK_DIR}

# to verify the empty directories are created
RUN mkdir -p ${WORK_DIR}
RUN mkdir -p ${BASE_DIR}

#Installing packages

USER root
#COPY httpd.conf /etc/httpd/conf/httpd.conf

#Timezone IST
RUN ln -sf /usr/share/zoneinfo/Asia/Kolkata /etc/localtime && echo "Asia/Kolkata" > /etc/timezone

RUN sed -i 's/^IncludeOptional/#&/' /etc/httpd/conf/httpd.conf
RUN sed -i 's|Listen 0.0.0.0:8080|Listen 80|g' /etc/httpd/conf/httpd.conf
RUN sed -i 's|User apache|User default|g' /etc/httpd/conf/httpd.conf
RUN sed -i 's|Group apache|Group root|g' /etc/httpd/conf/httpd.conf


#RUN printf '\nLoadModule headers_module modules/mod_headers.so\n' >> /etc/httpd/conf/httpd.conf
#RUN printf '\n<IfModule mod_headers.c>\n    Header always set Access-Control-Allow-Origin "*"\n    Header always set Access-Control-Allow-Headers "X-Requested-With, Content-Type, _csrf"\n    Header always set Access-Control-Allow-Methods "GET, POST, PUT, DELETE, OPTIONS"\n    Header always set Access-Control-Allow-Credentials "true"\n</IfModule>\n' >> /etc/httpd/conf/httpd.conf

#RUN printf "\n\
#SetEnvIfNoCase ^_csrf\$ ^(.*)\$ fix_csrf=\$1\n\
#RequestHeader set X-CSRF-Token %%{fix_csrf}e env=fix_csrf\n\
#" >> /etc/httpd/conf/httpd.conf



COPY ${SERVICE_NAME} ${WORK_DIR}


RUN unzip -q ${WORK_DIR}/${SERVICE_NAME} -d ${WORK_DIR}
RUN mv  ${WORK_DIR}/e-migrate/* ${BASE_DIR}

EXPOSE 80


CMD ["httpd", "-D", "FOREGROUND"]

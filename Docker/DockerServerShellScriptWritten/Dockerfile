FROM httpd:2.4

ARG BUILD_ID            xx
ENV SERVICE_NAME        e-migrate-withoutCaptcha-DOCKER-DEV.zip
ENV VERSION             0.0.1-SNAPSHOT
ENV SERVICE_PORT        8081
ARG ENVIRONMENT         QA
ENV WORK_DIR            /var/www/html

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
RUN mkdir -p ${WORK_DIR}/newbuild
RUN mkdir -p ${WORK_DIR}/newbuild/html

#RUN yum install unzip
RUN apt-get update && apt-get install -y unzip
COPY ${SERVICE_NAME} ${WORK_DIR}/newbuild
RUN unzip ${WORK_DIR}/newbuild/${SERVICE_NAME} -d ${WORK_DIR}/newbuild
#RUN ls -al /usr/local/apache2/htdocs/ 
#RUN cd /usr/local/apache2/htdocs/e-migrate
RUN mkdir /usr/local/apache2/htdocs/emigrateSIT
RUN mv  ${WORK_DIR}/newbuild/e-migrate/* /usr/local/apache2/htdocs/emigrateSIT
#RUN cd ${WORK_DIR}
#RUN mv /usr/local/apache2/htdocs/e-migrate/ /usr/local/apache2/htdocs/
#RUN unzip ${WORK_DIR}/newbuild/${SERVICE_NAME} -d /public-html
#RUN ls -al ${WORK_DIR}/newbuild/html
#RUN ls -al ${WORK_DIR}/newbuild
#RUN ls -al ${WORK_DIR}/
#COPY ./public-html/ /usr/local/apache2/htdocs/
#COPY ${WORK_DIR}/newbuild/html/   /usr/local/apache2/htdocs/


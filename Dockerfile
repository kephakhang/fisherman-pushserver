FROM amazoncorretto:latest

ENV APPLICATION_USER korbit
ENV APP_NAME fisherman-pushserver

MAINTAINER Korbit Developer <developer@korbit.co.kr>


RUN mkdir -p /$APPLICATION_USER
RUN mkdir -p /$APPLICATION_USER/jks
RUN mkdir -p /$APPLICATION_USER/log

WORKDIR /$APPLICATION_USER

COPY ./build/libs/$APP_NAME.jar $APP_NAME.jar
COPY ./jks/key.jks jks/key.jks
COPY ./run.sh run.sh
COPY ./stop.sh stop.sh
RUN chmod 774 run.sh stop.sh

RUN ["chown", "-R", "daemon", "."]

USER daemon


ENTRYPOINT ["./run.sh"]

CMD []

EXPOSE 8080



#!/bin/sh

/bin/rm -rf ./build/*

gradle -Pprofile=local build

#export PWD =`pwd`

zip -r fisherman-pushserver.zip Dockerfile Dockerrun.aws.json jks/key.jks build/libs/fisherman-pushserver.jar run.sh stop.sh .ebextensions



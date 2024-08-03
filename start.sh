#!/bin/sh

java  -server -Xms4g -Xmx4g -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:+UseStringDeduplication -jar ./build/libs/fisherman-pushserver.jar

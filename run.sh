#!/bin/sh


#export LANG=ko_KR.utf8
#kafka zoo
#sleep 30
#kafka start
#sleep 30

#exec java  -server -Xms60G -Xmx60G -XX:+MaxFDLimit -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:+UseStringDeduplication \
exec java  -server -Dfile.encoding=UTF8 -XX:+UseG1GC -XX:+DisableExplicitGC -XX:+UseStringDeduplication \
 -Xms3072m -Xmx3072m -verbosegc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:./gc.log \
 -Djava.net.preferIPv4Stack=true -Dcom.ning.http.client.AsyncHttpClientConfig.allowPoolingConnection=false \
 -jar fisherman-pushserver.jar

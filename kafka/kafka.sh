#!/bin/sh

export JAVA_HOME=/usr/local/Cellar/jdk/amazon-corretto-11.jdk/Contents/Home

zoo() {
	export PWD=`pwd`
	cd /usr/local/Cellar/kafka/2.12-2.3.1
	bin/zookeeper-server-start.sh config/zookeeper.properties &
	cd $PWD
}

start() {
	export PWD=`pwd`
	cd /usr/local/Cellar/kafka/2.12-2.3.1
	bin/kafka-server-start.sh config/server.properties &
	cd $PWD
}

stop() {
	export PWD=`pwd`
    cd /usr/local/Cellar/kafka/2.12-2.3.1
    bin/kafka-server-stop.sh config/server.properties
    bin/zookeeper-server-stop.sh config/zookeeper.properties
	cd $PWD
}

config() {
	export PWD=`pwd`
	cd /usr/local/Cellar/kafka/2.12-2.3.1
	bin/kafka-topics.sh  --describe --bootstrap-server localhost:9092 --topic $2
	cd $PWD
}

create() {
	export PWD=`pwd`
	cd /usr/local/Cellar/kafka/2.12-2.3.1
	bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic $2 
	cd $PWD
}

produce() {
	export PWD=`pwd`
	cd /usr/local/Cellar/kafka/2.12-2.3.1
	bin/kafka-console-producer.sh  --broker-list localhost:9092 --topic $2 
	cd $PWD
}

consume() {
	export PWD=`pwd`
	cd /usr/local/Cellar/kafka/2.12-2.3.1
	bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic $2 --from-beginning
    cd $PWD
}

list() {
	export PWD=`pwd`
	cd /usr/local/Cellar/kafka/2.12-2.3.1
	bin/kafka-topics.sh --list --bootstrap-server localhost:9092
    cd $PWD
}

delete() {
	export PWD=`pwd`
	cd /usr/local/Cellar/kafka/2.12-2.3.1
	bin/kafka-topics.sh --bootstrap-server localhost:9092 --delete --topic $2
    cd $PWD
}

shell() {
	export PWD=`pwd`
	cd /usr/local/Cellar/kafka/2.12-2.3.1
	bin/zookeeper-shell.sh localhost:2181
    cd $PWD
}

main() {
	echo $1 $2

	if [ "$1" = "zoo" ]; then
		zoo
	elif [ "$1" = "start" ]; then
		start
	elif [ "$1" = "stop" ]; then
		stop
	elif [ "$1" = "shell" ]; then
		shell
	elif [ "$1" = "list" ]; then
		list
	elif [ "$1" = "create" ] && [ $# = 2 ]; then
		create $1 $2
	elif [ "$1" = "write" ]  && [ $# = 2 ]; then
		produce $1 $2
	elif [  "$1" = "read" ]  && [ $# = 2 ]; then
		consume $1 $2
	elif [  "$1" = "delete" ]  && [ $# = 2 ]; then
		delete $1 $2
	elif [  "$1" = "clear" ]  && [ $# = 2 ]; then
		delete $1 $2
	elif [  "$1" = "config" ]  && [ $# = 2 ]; then
		config $1 $2
	else
		echo 'kafka zoo|start|stop|list|create topic|write topic|read topic|delete topic|config topic'
	fi
}

main $1 $2

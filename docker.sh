#!/bin/sh

docker build -t kephakhang/fisherman-pushserver:0.1.3 . -f Dockerfile

#docker run --rm -it -p 127.0.0.1:80:8080 kephakhang/fisherman-pushserver:0.0.7
# http://127.0.0.1:80
#docker run -p 80:8080  --ulimit nofile=65535:65535 kephakhang/fisherman-pushserver:0.1.0
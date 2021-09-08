#!/bin/bash
mkdir -p ~/app/front-end
mkdir -p ~/app/back-end/properties
mkdir -p ~/app/back-end/logs
mkdir -p ~/app/back-end/logstash/pipeline

curl -v -X GET https://raw.githubusercontent.com/bartoszkordek/AGH-Praca-inzynierska-back-end/main/docker-compose-front.yaml > ~/app/front-end/docker-compose-front.yaml
curl -v -X GET https://raw.githubusercontent.com/bartoszkordek/AGH-Praca-inzynierska-back-end/main/docker-compose.yaml > ~/app/back-end/docker-compose.yaml
curl -v -X GET https://raw.githubusercontent.com/bartoszkordek/AGH-Praca-inzynierska-back-end/main/properties/application-example.properties > ~/app/back-end/properties/application.properties
curl -v -X GET https://raw.githubusercontent.com/bartoszkordek/AGH-Praca-inzynierska-back-end/main/logstash/pipeline/logstash.conf > ~/app/back-end/logstash/pipeline/logstash.conf
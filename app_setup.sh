#!/bin/bash
mkdir -p ~/app/front-end
mkdir -p ~/app/back-end/properties

curl -v -X GET https://raw.githubusercontent.com/bartoszkordek/AGH-Praca-inzynierska-back-end/main/docker-compose-front.yaml > ~/app/front-end/docker-compose-front.yaml
curl -v -X GET https://raw.githubusercontent.com/bartoszkordek/AGH-Praca-inzynierska-back-end/main/docker-compose.yaml > ~/app/back-end/docker-compose.yaml
curl -v -X GET https://raw.githubusercontent.com/bartoszkordek/AGH-Praca-inzynierska-back-end/main/properties/application-example.properties > ~/app/back-end/properties/application.properties
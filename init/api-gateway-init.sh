#!/bin/bash

cd ~/Documents/Harsh/'Not Games'/JAVA/Restaurant-Management-System/api-gateway/
mvn clean install
cd target
java -jar api-gateway-0.0.1-SNAPSHOT.jar 

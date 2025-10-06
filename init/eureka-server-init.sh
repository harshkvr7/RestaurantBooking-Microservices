#!/bin/bash

cd ~/Documents/Harsh/'Not Games'/JAVA/Restaurant-Management-System/eureka-server/
mvn clean install
cd target
java -jar eureka-server-0.0.1-SNAPSHOT.jar 

#!/bin/bash

cd ~/Documents/Harsh/'Not Games'/JAVA/Restaurant-Management-System/user-service/
mvn clean install
cd target
java -jar user-service-0.0.1-SNAPSHOT.jar

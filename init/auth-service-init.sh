#!/bin/bash

cd ~/Documents/Harsh/'Not Games'/JAVA/Restaurant-Management-System/auth-service/
mvn clean install
cd target
java -jar auth-service-0.0.1-SNAPSHOT.jar

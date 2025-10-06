#!/bin/bash

cd ~/Documents/Harsh/'Not Games'/JAVA/Restaurant-Management-System/notification-service/
mvn clean install
cd target
java -jar notification-service-0.0.1-SNAPSHOT.jar

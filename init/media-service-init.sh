#!/bin/bash

cd ~/Documents/Harsh/'Not Games'/JAVA/Restaurant-Management-System/media-service/
mvn clean install
cd target
java -jar media-service-0.0.1-SNAPSHOT.jar

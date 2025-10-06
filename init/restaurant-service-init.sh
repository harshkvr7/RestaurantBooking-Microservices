#!/bin/bash

cd ~/Documents/Harsh/'Not Games'/JAVA/Restaurant-Management-System/restaurant-service
mvn clean install
cd target
java -jar restaurant-service-0.0.1-SNAPSHOT.jar

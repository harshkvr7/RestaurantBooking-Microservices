#!/bin/bash

cd ~/Documents/Harsh/'Not Games'/JAVA/Restaurant-Management-System/reservation-service
mvn clean install
cd target
java -jar reservation-service-0.0.1-SNAPSHOT.jar

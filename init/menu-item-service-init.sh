#!/bin/bash

cd ~/Documents/Harsh/'Not Games'/JAVA/Restaurant-Management-System/menu-item-service/
mvn clean install
cd target
java -jar menu-item-service-0.0.1-SNAPSHOT.jar

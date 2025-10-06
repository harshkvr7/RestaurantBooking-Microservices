#!/bin/bash

export API_GATEWAY_URL="http://localhost:8080/"
export USER_SERVICE_URL="http://localhost:8082/"
export AUTH_SERVICE_URL="http://localhost:8081/"
export RESTAURANT_SERVICE_URL="http://localhost:8083/"
export MEDIA_SERVICE_URL="http://localhost:8084/"
export RESERVATION_SERVICE_URL="http://localhost:8085/"
export MENU_ITEM_SERVICE_URL="http://localhost:8087/"
export ANALYTICS_SERVICE_URL="http://localhost:8088/"


export MINIO_URL="http://localhost:9000"
export MINIO_ACCESS_KEY="minioadmin"
export MINIO_SECRET_KEY="minioadmin"

export EUREKA_URL="http://localhost:8761/eureka/"
export SPRING_KAFKA_BOOTSTRAP_SERVERS="localhost:9092"

export JWT_SECRET="0e13cad64f7bc6516065b474885bb39e372005225c7d3af97ec6755ac726ed8a"
export JWT_EXPIRATION=36000000

export DB_USERNAME="postgres"
export DB_PASSWORD="postgres"
export MENU_ITEM_DB_URL="jdbc:postgresql://localhost:5435/menu_items_db"
export RESERVATIONS_DB_URL="jdbc:postgresql://localhost:5434/reservations_db"
export RESTAURANTS_DB_URL="jdbc:postgresql://localhost:5433/restaurants_db"
export USERS_DB_URL="jdbc:postgresql://localhost:5432/users_db"
export ANALYTICS_DB_URL="jdbc:postgresql://localhost:5437/analytics_db"

export EMAIL_USERNAME="restaurantmanager189@gmail.com"
export EMAIL_PASSWORD="gwst zwfw rcce cvkx"

export LOCATION_REDIS_HOST="localhost"
export LOCATION_REDIS_PORT="6379"


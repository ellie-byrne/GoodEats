#!/bin/bash

# Start Eureka Server
echo "Starting Eureka Server..."
cd backend/goodEats-microservices/eureka-server
mvn spring-boot:run &
sleep 15  # Wait for Eureka to start

# Start other services
echo "Starting User Service..."
cd ../user-service
mvn spring-boot:run &
sleep 10

echo "Starting Restaurant Service..."
cd ../restaurant-service
mvn spring-boot:run &
sleep 10

echo "Starting Review Service..."
cd ../review-service
mvn spring-boot:run &
sleep 10

echo "Starting Static Content Service..."
cd ../static-content-service
mvn spring-boot:run &
sleep 10

# Start API Gateway last
echo "Starting API Gateway..."
cd ../api-gateway
mvn spring-boot:run
#!/bin/bash

# Kill all Java processes
pkill -f java

# Check for any processes still using the ports
for PORT in 8080 8082 8761; do
  PID=$(lsof -ti:$PORT)
  if [ -n "$PID" ]; then
    echo "Killing process on port $PORT"
    kill -9 $PID
  fi
done

sleep 2

# Start just the essential services
cd eureka-server
echo "Starting Eureka Server..."
mvn spring-boot:run > ../eureka.log 2>&1 &
sleep 15

cd ../restaurant-service
echo "Starting Restaurant Service..."
mvn spring-boot:run > ../restaurant.log 2>&1 &
sleep 10

cd ../api-gateway
echo "Starting API Gateway..."
mvn spring-boot:run > ../gateway.log 2>&1 &
sleep 5

cd ../static-content-service
echo "Starting Static Content Service..."
mvn spring-boot:run > ../static.log 2>&1 &

echo "Services started! Access the application at http://localhost:8080"
echo "Check log files if issues persist."
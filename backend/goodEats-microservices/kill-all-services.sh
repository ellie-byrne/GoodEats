#!/bin/bash

# Script to completely kill all processes using microservice ports
echo "Stopping all microservices..."

# Get process IDs for Java applications
JAVA_PIDS=$(ps -ef | grep java | grep spring-boot | awk '{print $2}')

if [ -n "$JAVA_PIDS" ]; then
  echo "Killing Java processes: $JAVA_PIDS"
  echo $JAVA_PIDS | xargs kill -9
else
  echo "No Java processes found."
fi

# Check and kill processes on specific ports
PORTS=(8080 8081 8082 8083 8084 8761)
for PORT in "${PORTS[@]}"; do
  echo "Checking port $PORT..."
  PID=$(lsof -ti:$PORT)
  if [ -n "$PID" ]; then
    echo "Killing process $PID using port $PORT"
    kill -9 $PID
  else
    echo "No process using port $PORT"
  fi
done

# Wait to ensure all processes are terminated
sleep 3
echo "All services should be stopped now."
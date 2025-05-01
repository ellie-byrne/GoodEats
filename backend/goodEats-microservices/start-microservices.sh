#!/bin/bash

# Run the cleanup script first to kill all processes
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

# Start services with proper error handling
echo "Starting microservices..."

# Function to check if a port is available
check_port() {
  if lsof -Pi :$1 -sTCP:LISTEN -t >/dev/null ; then
    return 1
  else
    return 0
  fi
}

# Function to start a service
start_service() {
  SERVICE_NAME=$1
  PORT=$2
  DIRECTORY=$3

  echo "Starting $SERVICE_NAME on port $PORT..."

  if ! check_port $PORT; then
    echo "ERROR: Port $PORT is already in use. Cannot start $SERVICE_NAME."
    return 1
  fi

  # Check if directory exists first
  if [ ! -d "$DIRECTORY" ]; then
    echo "ERROR: Directory $DIRECTORY not found!"
    return 1
  fi

  # Save current directory
  CURRENT_DIR=$(pwd)

  # Navigate to service directory
  cd "$DIRECTORY" || { echo "Cannot change to directory $DIRECTORY"; return 1; }

  # Start the service
  echo "Starting $SERVICE_NAME in directory $(pwd)"
  mvn spring-boot:run > "$CURRENT_DIR/$SERVICE_NAME.log" 2>&1 &
  PID=$!

  # Return to original directory
  cd "$CURRENT_DIR"

  # Wait a moment to check if process is still running
  sleep 5
  if ps -p $PID > /dev/null; then
    echo "$SERVICE_NAME started successfully on port $PORT (PID: $PID)"
    return 0
  else
    echo "ERROR: $SERVICE_NAME failed to start. Check $SERVICE_NAME.log for details."
    return 1
  fi
}

# Use the current directory as base
BASE_DIR="$(pwd)"
echo "Using base directory: $BASE_DIR"

# List available services
echo "Available services:"
ls -la

# Start Eureka Server first
start_service "eureka-server" 8761 "eureka-server"
if [ $? -ne 0 ]; then
  echo "Failed to start Eureka Server. Exiting."
  exit 1
fi

# Wait for Eureka to initialize
echo "Waiting for Eureka to initialize..."
sleep 15

# Start the rest of the services
start_service "user-service" 8081 "user-service"
sleep 5
start_service "restaurant-service" 8082 "restaurant-service"
sleep 5
start_service "review-service" 8083 "review-service"
sleep 5
start_service "static-content-service" 8084 "static-content-service"
sleep 5
start_service "api-gateway" 8080 "api-gateway"

echo "All services started! Check individual log files for any errors."
echo "API Gateway: http://localhost:8080"
echo "Eureka Dashboard: http://localhost:8761"
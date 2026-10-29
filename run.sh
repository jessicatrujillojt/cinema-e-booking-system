#!/bin/bash

cd "$(dirname "$0")"

export JAVA_HOME=$(/usr/libexec/java_home)

cleanup() {
    echo
    echo "Stopping backend and frontend..."

    kill $BACKEND_PID 2>/dev/null
    kill $FRONTEND_PID 2>/dev/null

    exit
}

trap cleanup INT TERM

echo "Starting backend..."
(
    cd backend
    ./mvnw spring-boot:run
) &
BACKEND_PID=$!

echo "Starting frontend on http://localhost:5501 ..."
python3 -m http.server 5501 &
FRONTEND_PID=$!

echo
echo "Cinema E-Booking System is running."
echo "Frontend: http://localhost:5501/home.html"
echo "Backend:  http://localhost:8080"
echo
echo "Press Ctrl+C to stop."

wait
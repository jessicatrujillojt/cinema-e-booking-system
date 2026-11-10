#!/bin/bash

cd "$(dirname "$0")"

export JAVA_HOME=$(/usr/libexec/java_home)

cleanup() {
    echo
    echo "Stopping Cinema E-Booking System..."

    # Stop anything this app is using on its two ports
    lsof -ti :8080 | xargs kill 2>/dev/null
    lsof -ti :5501 | xargs kill 2>/dev/null

    echo "Backend and frontend stopped."
    exit 0
}

trap cleanup INT TERM EXIT

echo "Starting backend..."
(
    cd backend
    ./mvnw spring-boot:run
) &

echo "Starting frontend on http://localhost:5501 ..."
python3 -m http.server 5501 &

echo
echo "Cinema E-Booking System is running."
echo "Frontend: http://localhost:5501/home.html"
echo "Backend:  http://localhost:8080"
echo
echo "Press Ctrl+C to stop."

wait
#!/bin/bash

cd "$(dirname "$0")"

export JAVA_HOME=$(/usr/libexec/java_home)

echo "Starting backend..."
(
  cd backend
  ./mvnw spring-boot:run
) &

echo "Starting frontend on http://localhost:5501 ..."
python3 -m http.server 5501 &

wait
#!/bin/bash
cd "$(dirname "$0")/backend"
./mvnw spring-boot:run

chmod +x run.sh
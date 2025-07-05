#!/bin/bash

# This script calls mount_frontend.sh and then starts backend

./mount_frontend.sh || { echo "Frontend building failed"; exit 1; }

cd backend

./mvnw spring-boot:run


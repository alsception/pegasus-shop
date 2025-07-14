#!/bin/bash

# This script only build frontend and backend without starting

echo "Building frontend..."                                                      
./mount_frontend.sh || { echo "Frontend building failed"; exit 1; }

cd backend

echo "Building backend..."                                                      
./mvnw clean package -DskipTests      


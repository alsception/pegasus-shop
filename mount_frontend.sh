#!/bin/bash

####################################################################################
# This file builds the frontend and mounts the content of /dist folder             #
# to the backend/src/main/resources/static folder (previosly deletes both).        #
# Frontend build is composed of index.html, js, css, and other.                    #
# index.html is served from spring boot and is the entry point of the application  #
# Author alsception@proton.me 5/7/2025                                             #
####################################################################################

# 0. Track start time
start_time=$(date +%s)

# 1. Set paths
FRONTEND_DIR="./frontend"
BACKEND_STATIC_DIR="./backend/src/main/resources/static"

# 2. Clean previous frontend build and backend static resources
echo "Removing old dist and static folders..."
rm -rf "$FRONTEND_DIR/dist"
rm -rf "$BACKEND_STATIC_DIR"

# 3. Build frontend
echo "Building frontend..."
echo "-------------------------------------------------------------"
cd "$FRONTEND_DIR" || exit
npm run build || { echo "Frontend build failed"; exit 1; }

# 4. Go back to project root
cd - > /dev/null || exit

# 5. Copy build output to Spring Boot static directory
echo "-------------------------------------------------------------"
echo "Copying build output to backend static folder..."
mkdir -p "$BACKEND_STATIC_DIR"
cp -r "$FRONTEND_DIR/dist/"* "$BACKEND_STATIC_DIR"

# 6. Track end time
end_time=$(date +%s)
duration=$((end_time - start_time))

# Done
echo "Deploy completed successfully in ${duration} seconds!"
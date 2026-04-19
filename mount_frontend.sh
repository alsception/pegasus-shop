#!/bin/bash

####################################################################################
# This file builds the frontend and mounts the content of /dist folder             #
# to the backend/src/main/resources/static folder (previosly deletes both).        #
# Frontend build is composed of index.html, js, css, and other.                    #
# index.html is served from spring boot and is the entry point of the application  #
# Author alsception@proton.me 5/7/2025                                             #
#                                                                                  #
# Update 17/4/26:                                                                  #
# Now we have 2 different frontends:                                               # 
# 1. what was previous frontend is now storefront                                  # 
# 2. separate /backoffice frontend application                                     #
# Both are still served from the same spring boot                                  #
####################################################################################

# 0. Track start time
start_time=$(date +%s)

# 1. Set paths
FRONTEND_DIR="./frontend"
FRONTEND_BO_DIR="./frontend_backoffice"
BACKEND_STATIC_DIR="./backend/src/main/resources/static"

# 2. Clean previous frontend build and backend static resources
echo "Removing old dist and static folders..."
rm -rf "$FRONTEND_DIR/dist"
rm -rf "$FRONTEND_BO_DIR/dist"
rm -rf "$BACKEND_STATIC_DIR"

# 3. Build store frontend
echo "Building store frontend..."
echo "-------------------------------------------------------------"
cd "$FRONTEND_DIR" || exit
npm run build || { echo "Store frontend build failed"; exit 1; }

# 4. Go back to project root
cd - > /dev/null || exit

# 5. Build backoffice frontend
echo "Building backoffice..."
echo "-------------------------------------------------------------"
cd "$FRONTEND_BO_DIR" || exit
npm run build || { echo "Backoffice build failed"; exit 1; }

# 6. Go back to project root
cd - > /dev/null || exit

# 7. Copy frontend build output to Spring Boot static directory
echo "-------------------------------------------------------------"
echo "Copying storefronte build output to backend static folder..."
mkdir -p "$BACKEND_STATIC_DIR"
cp -r "$FRONTEND_DIR/dist/"* "$BACKEND_STATIC_DIR"

# 8. Copy backoffice frontend build output to Spring Boot static directory /backoffice
echo "Copying backoffice build output to backend static folder..."
mkdir -p "$BACKEND_STATIC_DIR/backoffice"
cp -r "$FRONTEND_BO_DIR/dist/"* "$BACKEND_STATIC_DIR/backoffice"

# 9. Track end time
end_time=$(date +%s)
duration=$((end_time - start_time))

# Done
echo "-------------------------------------------------------------"
echo "Frontends mounting completed successfully in ${duration} seconds!"

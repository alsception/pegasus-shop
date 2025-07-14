#!/bin/bash                                                                     

# This script builds 1-frontend,2-backend,3-docker image and deploys is to dockerhub                   
# Autor alsception@proton.me 11/7/25                                            
                                             
echo "Mounting frontend..."                                                      
./mount_frontend.sh || { echo "Frontend building failed"; exit 1; }
                                   
cd backend                                                                      
                                                                                
echo "Building spring project..."                                                      
./mvnw clean package -DskipTests                                                
                                                                                
echo "Building docker image..."                                                 
docker build -t alsception/pegasus .                                            
                                                                                
echo "Deploying to dockerhub"                                                   
docker push alsception/pegasus:latest

echo "Done."

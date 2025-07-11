#!/bin/bash                                                                     

# This script builds docker image and deploys is to dockerhub                   
# Autor alsception@proton.me 11/7/25                                            
                                                                                
cd backend                                                                      
                                                                                
echo "Building project..."                                                      
./mvnw clean package -DskipTests                                                
                                                                                
echo "Building docker image..."                                                 
docker build -t alsception/pegasus .                                            
                                                                                
echo "Deploying to dockerhub"                                                   
docker push alsception/pegasus:latest

echo "Done."

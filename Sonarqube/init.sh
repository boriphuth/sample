#!/bin/bash

# prepare directory structure
mkdir -p volumes/conf
mkdir -p volumes/extensions
mkdir -p volumes/data
chmod -R 777 ./volumes 

# run sonar init
docker run --rm \
-v $PWD/volumes/conf:/opt/sonarqube/conf \
-v $PWD/volumes/extensions:/opt/sonarqube/extensions \
-v $PWD/volumes/data:/opt/sonarqube/data \
sonarqube:community-beta --init

# fix permissions again
find ./volumes -type d -exec sudo chmod 777 {} + 

# copy sonar properties into conf dir
sudo cp sonar.properties ./volumes/conf/
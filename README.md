# sample
sudo sysctl -w vm.max_map_count=262144
sudo sysctl -w fs.file-max=262144
docker-compose -f Sonarqube/sonar.yml up -d
docker-compose -f Anchore-Engine/docker-compose.yaml up -d
./Sonarqube/init.sh && docker-compose -f Sonarqube/sonar.yml up -d
./init.sh && docker-compose up -d.
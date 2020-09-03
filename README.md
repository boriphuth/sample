# sample
sudo sysctl -w vm.max_map_count=262144
sudo sysctl -w fs.file-max=262144
docker-compose -f Sonarqube/sonar.yml up -d
./Sonarqube/init.sh && docker-compose -f Sonarqube/sonar.yml up -d
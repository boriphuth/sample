# Anchore Engine

The Anchore Engine is an open source project that provides a centralized service for performing detailed analysis on container images, running queries, producing reports and defining ploicies that can be used in CI/CD pipelines. This guide helps set up anchore engine container so that it can be integrated with the jenkins using anchore engine plugin.

# Pre-requisite

- Must have docker and docker-compose installed on the host.

# Getting Started

  - Clone this repository and move to the Anchore-Engine directory.
    ```
    git clone https://github.com/Devops-Accelerators/DevSecOps.git && cd DevSecOps/Anchore-Engine
    ```
  - Create a directory to persist data.
    ```
    mkdir -p db
    ```
  - ***Do ensure to change the password in the config.yaml***
    ```
    credentials:
      users:
         admin:
           password: 'your_password_here'
           email: 'admin@myemail.com'
           external_service_auths:
           #  anchoreio:
           #    anchorecli:
           #      auth: 'myanchoreiouser:myanchoreiopass'
           #auto_policy_sync: True
    ```
    
  - Finally, run the docker image.
    ```
    docker-compose up -d
    ```
  - Follow the below link to understand how anchore engine can be integrated with jenkins.
    [Integrate Anchore engine with Jenkins](https://wiki.jenkins.io/display/JENKINS/Anchore+Container+Image+Scanner+Plugin)
    

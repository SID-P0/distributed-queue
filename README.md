**Running the project in local**
1. docker compose -f docker-compose-dev.yml up -d
2. docker compose -f docker-compose-infra.yml up -d (Ignore if you dont want to re-link and replicate all the infra to run it locally)

To run both together
1. docker-compose -p devenv -f docker-compose-infra.yml -f docker-compose-dev.yml up -d


Volume mount the local code to build and run the application
1. docker-compose -p devenv -f docker-compose-dev.yml exec dev-env ./gradlew build
2. docker-compose -p devenv -f docker-compose-dev.yml exec dev-env ./gradlew bootRun -PappJvmArgs="-Dserver.port=9090 -Dspring.profiles.active=dockerdev"


**IF YOU WANT TO RUN INFRA YOU MUST RUN DEV ENV IN ORDER TO LOCALLY SUPPORT NETWORK BRIDGE**

![Test drawio](https://github.com/user-attachments/assets/a7be76a4-c4d3-4b73-bb10-bf21f23344f9)

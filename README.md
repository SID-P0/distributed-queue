**Running the project in local**
1. docker compose -p distributedqueue -f docker-compose-dev.yml up -d
2. docker compose -p distributedqueue -f docker-compose-infra.yml up -d (Ignore if you dont want to re-link and replicate all the infra to run it locally)

To run both together
1. docker compose -p distributedqueue -f docker-compose-infra.yml -f docker-compose-dev.yml up -d

Volume mount the local code to build and run the application
1. docker compose -p devenv -f docker-compose-dev.yml exec dev-env ./gradlew build
2. docker compose -p devenv -f docker-compose-dev.yml exec dev-env ./gradlew bootRun -PappJvmArgs="-Dserver.port=9090 -Dspring.profiles.active=dockerdev"

 If you are using a remote dev container
1. Figure out the port for postgres connection which
   docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' ####-postgres-1 (app-name in infra.yml)
2. Change kafka bootstrap server to kafka:#### (broker host in infra.yml)

**IF YOU WANT TO RUN INFRA YOU MUST RUN DEV ENV IN ORDER TO LOCALLY SUPPORT NETWORK BRIDGE**


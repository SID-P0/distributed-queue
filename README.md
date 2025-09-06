**Distributed Job Queue System**

A distributed job queue system designed to manage and coordinate complex job orchestration across multiple services. The system supports both automated and manually-triggered jobs, leveraging a load balancer and orchestrator to efficiently publish tasks to a Kafka queue.

Each job can encompass multiple tasks, allowing for granular execution and easy scaling. Dedicated consumer services process queued jobs, updating their status in real-time through a central job status topic. Comprehensive tracking is achieved via server-side events and audit tables, ensuring that all dependencies and metadata are effectively managed.

The architecture enables reliable status updates to upstream components, allowing services to respond dynamically to job dependencies and ensuring seamless orchestration throughout the workflow.

Proposed architecture diagram

<img width="720" height="820" alt="image" src="https://github.com/user-attachments/assets/95bcbd56-ebf2-428c-9027-dbbde3a95e4e" />


Proposed Schema

<img width="722" height="874" alt="image" src="https://github.com/user-attachments/assets/997704aa-a6e7-47d2-8b76-e7e2faaaf235" />



**Steps for Running the project locally**
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


**TODO**
1. Parallel processing of jobs ( POC )
2. Retry Mechanism (DLQ/Api retries)

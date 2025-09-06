**Distributed Job Queue System**

A distributed job queue system engineered to coordinate and execute complex jobs, where each job can comprise multiple tasks. It supports both automated and manual job triggering, using a load balancer and orchestrator to efficiently dispatch tasks via a Kafka-backed queue.

Key features include built-in rate limiting to ensure fair resource usage and prevent system overload, as well as parallel processing capabilities for different tasks, maximizing throughput and responsiveness. Multiple independent consumer services process queued jobs concurrently, updating job statuses in real time through a central topic.

Robust tracking is achieved with server-side events and audit tables, ensuring all dependencies and metadata are properly managed. The system enables reliable status propagation to upstream components so services can respond dynamically to job dependencies, facilitating seamless orchestration, compliance, and transparency throughout the entire workflow.

**Proposed architecture diagram**

<img width="720" height="820" alt="image" src="https://github.com/user-attachments/assets/95bcbd56-ebf2-428c-9027-dbbde3a95e4e" />


**Proposed Schema**

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

# jee-container-and-db
Playground repo for learning Docker Compose

Experimenting with creating a two-containers compose deployment. 
One will have a JBoss EAP hosted Web App (based on Java 7!); the other one a Development version of an Oracle DB...

## Running

### Prerequisite

Since this system relies on an Oracle image, you need to have a valid Oracle account to access the 
[registry](https://container-registry.oracle.com/). 
Please ensure you've accepted the terms and conditions (follow instructions 
[here](https://collabnix.com/how-to-run-oracle-database-in-a-docker-container-using-docker-compose/)).

### Starting the containers

```shell
docker compose up -d 
```
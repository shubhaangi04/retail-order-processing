## Retail Order Processing
An order processing API which uses MongoDB for persisting order state
and Kafka as a queue to manage order processing.  

### Technologies Used
- Java
- Spring Boot
- MongoDB
- Kafka
- JUnit
- Mockito

### Local Setup
Run the docker-compose up command on the root of the project. 
It uses the defined docker-compose.yml to 
bring up all the downstream dependencies of the project 
such as ZooKeeper, Kafka, and MongoDB.

#### Command to create kafka topic
```
docker exec kafka \
kafka-topics --bootstrap-server kafka:9092 \
             --create \
             --topic order-processing
```
# SOA: Driver Microservices with 3 Instances, Round-Robin Load Balancing & API Gateway

This Maven multi-module project contains:
- **discovery-server** — Eureka registry on `8761`
- **driver-service** — one service artifact that you run as three instances on `8081`, `8082`, and `8083`
- **api-gateway** — gateway on `8080`, routing `/drivers/**` to `lb://DRIVER-SERVICE`

Spring Cloud LoadBalancer's default `RoundRobinLoadBalancer` distributes requests across the healthy registered driver instances in round-robin order. No custom load-balancer code is required.

## Requirements
- JDK 17+
- Maven 3.9+
- Internet connection for the first Maven dependency download

## Build
From this folder:
```bash
mvn clean package
```

## Run (four terminals, from the project root)

**Terminal 1 — Eureka registry**
```bash
mvn -pl discovery-server spring-boot:run
```

**Terminal 2 — Driver instance 1**
```bash
mvn -pl driver-service spring-boot:run -Dspring-boot.run.arguments="--server.port=8081 --eureka.instance.instance-id=driver-8081"
```

**Terminal 3 — Driver instance 2**
```bash
mvn -pl driver-service spring-boot:run -Dspring-boot.run.arguments="--server.port=8082 --eureka.instance.instance-id=driver-8082"
```

**Terminal 4 — Driver instance 3**
```bash
mvn -pl driver-service spring-boot:run -Dspring-boot.run.arguments="--server.port=8083 --eureka.instance.instance-id=driver-8083"
```

**Terminal 5 — API Gateway**
```bash
mvn -pl api-gateway spring-boot:run
```

Wait about 15–30 seconds for registration and discovery. Open the Eureka dashboard:
`http://localhost:8761`

## Test the gateway
Send repeated requests to the gateway (not directly to the driver ports):
```bash
curl http://localhost:8080/drivers
curl http://localhost:8080/drivers
curl http://localhost:8080/drivers
curl http://localhost:8080/drivers/101
```
Inspect `instanceId` and `port` in each response. With all three instances healthy, requests should rotate among the registered instances. Discovery and load-balancer caches may mean the first few requests do not immediately show a perfect 8081 → 8082 → 8083 sequence.

## Gateway route
The configured route is:
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: drivers-route
          uri: lb://DRIVER-SERVICE
          predicates:
            - Path=/drivers/**
```
The gateway preserves the `/drivers/...` path and forwards it to the selected driver-service instance.

## Useful URLs
- Eureka dashboard: `http://localhost:8761`
- Gateway drivers endpoint: `http://localhost:8080/drivers`
- Gateway driver detail: `http://localhost:8080/drivers/101`
- Direct instance checks: `http://localhost:8081/drivers`, `http://localhost:8082/drivers`, `http://localhost:8083/drivers`

## Notes
- Start Eureka first, then all three driver instances, then the gateway.
- If a port is busy, stop the process using it or change the port consistently.
- This is a local learning/demo setup; add authentication, validation, centralized configuration, observability, and production deployment settings before production use.

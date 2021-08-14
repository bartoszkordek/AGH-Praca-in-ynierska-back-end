FROM maven:3.6.3-jdk-11-slim
WORKDIR /config-service
COPY . .
RUN mvn clean package
CMD ["mvn", "spring-boot:run", "-Dspring-boot.run.arguments=--spring.profiles.active=docker"]
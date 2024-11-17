# Step 1: Use an official OpenJDK image as a base image
FROM openjdk:17-jdk-slim

# Step 2: Set the working directory inside the container
WORKDIR /app

# Step 3: Copy the built JAR file from the host to the container
# Note: Make sure to replace the filename with the actual JAR file name from your build/libs/ directory.
COPY build/libs/sleep-service-0.0.1-SNAPSHOT.jar /app/app.jar

# Step 4: Expose the port that Eureka Server will run on
EXPOSE 8838

# Step 5: Run the Eureka Server application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

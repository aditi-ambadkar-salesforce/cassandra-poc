# Use an official OpenJDK runtime as a parent image
FROM openjdk:11-jre-slim

# Set the working directory in the container
WORKDIR /app

# Copy the current directory contents into the container
COPY target/cassandra-poc.jar /app/cassandra-poc.jar

# Expose the port your application will run on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "cassandra-poc.jar"]

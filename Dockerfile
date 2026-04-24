# ---- Build Stage ----
# Uses the official Eclipse Temurin JDK 21 image to compile the application
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copy the Maven wrapper and project files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# Make the Maven wrapper executable and build the application
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# ---- Run Stage ----
# Uses a smaller JRE image to run the application. This keeps the final image size small.
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Create an entrypoint script to pass environment variables
RUN echo '#!/bin/sh' > /entrypoint.sh \
    && echo 'exec java -jar app.jar' >> /entrypoint.sh \
    && chmod +x /entrypoint.sh

# Expose the port your app runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["/entrypoint.sh"]

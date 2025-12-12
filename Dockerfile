# --- Stage 1: The "Build" Stage ---
# We start with a full Java JDK image to build our app
FROM eclipse-temurin:17-jdk-jammy AS builder

# Set the working directory inside the container
WORKDIR /workspace/app

# Copy the Maven "wrapper" - this lets us build without installing Maven
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Ensure the script is executable on Linux
RUN chmod +x mvnw

# Download all the dependencies
RUN ./mvnw dependency:go-offline

# Copy the rest of our source code
COPY src ./src

# Run the build! This will compile, run tests, and create the .jar
RUN ./mvnw package -DskipTests


# --- Stage 2: The "Run" Stage ---
# Now, we start with a *much smaller* image that *only* has Java to run
FROM eclipse-temurin:17-jre-jammy

# Set the working directory
WORKDIR /app

# Copy *only* the final .jar file from the "builder" stage
COPY --from=builder /workspace/app/target/*.jar app.jar

# Expose port 8080 to the outside world
EXPOSE 8080

# This is the command that runs when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]
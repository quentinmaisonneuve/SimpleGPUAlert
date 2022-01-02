FROM arm64v8/openjdk:17-jdk-slim
RUN mkdir -p app/simple-gpu-alert
WORKDIR /app/simple-gpu-alert
COPY SimpleGPUAlert-1.0-SNAPSHOT-jar-with-dependencies.jar SimpleGPUAlert-1.0.jar
CMD java -jar SimpleGPUAlert-1.0.jar
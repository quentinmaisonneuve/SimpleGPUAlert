FROM arm64v8/openjdk:17-jdk-slim
RUN mkdir -p app/simple-gpu-alert
WORKDIR /app/simple-gpu-alert
COPY SimpleGPUAlert-{version}.jar SimpleGPUAlert-{version}.jar
CMD java -jar SimpleGPUAlert-{version}.jar

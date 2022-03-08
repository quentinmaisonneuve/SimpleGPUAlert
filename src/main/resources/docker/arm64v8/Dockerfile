FROM arm64v8/openjdk:17-jdk-slim
RUN mkdir -p app/simple-gpu-alert
WORKDIR /app/simple-gpu-alert
COPY /target/SimpleGPUAlert-${project.version}.jar SimpleGPUAlert-${project.version}.jar
CMD java -jar SimpleGPUAlert-${project.version}.jar

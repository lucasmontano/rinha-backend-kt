# Use a imagem oficial do Gradle para construir o projeto
FROM gradle:jdk11 AS build

# Copie o projeto para o contêiner
COPY . /app
WORKDIR /app

# Construa o projeto
RUN gradle clean build -x test

# Use a imagem oficial do OpenJDK para executar o projeto
FROM openjdk:11-jre-slim

# Copie o JAR construído para a imagem
COPY --from=build /app/build/libs/*.jar /app/app.jar

# Porta em que o serviço será exposto
EXPOSE 8080

# Comando para iniciar a aplicação
CMD ["java", "-jar", "/app/app.jar"]

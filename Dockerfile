## Dockerfile
## Основной образ - если не работает, используйте альтернативы ниже
##FROM openjdk:17-jdk-slim
#
## Альтернативные образы (раскомментируйте если основной не работает):
## FROM eclipse-temurin:17-jdk
##FROM amazoncorretto:17
#FROM openjdk:17.0.1-slim
#
## Устанавливаем системные зависимости
#RUN apt-get update && apt-get install -y \
#    curl \
#    && rm -rf /var/lib/apt/lists/*
#
## Создаем директорию приложения
#WORKDIR /app
#
## Копируем JAR файл
#COPY target/*.jar app.jar
#
## Создаем директории для данных
#RUN mkdir -p /app/file-storage /app/logs
#
## Делаем пользователя владельцем директорий
#RUN chmod -R 755 /app/file-storage /app/logs
#
## Открываем порт приложения
#EXPOSE 8082
#
## Запускаем приложение
#ENTRYPOINT ["java", "-jar", "app.jar"]
# Этап сборки
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mkdir -p /app/file-storage /app/logs
RUN chmod -R 755 /app/file-storage /app/logs
RUN mvn clean package -DskipTests

# Этап запуска
FROM openjdk:17.0.1-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","app.jar"]
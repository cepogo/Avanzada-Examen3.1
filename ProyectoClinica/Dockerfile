# Usar una imagen base de Java
FROM openjdk:21-jdk-alpine

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el archivo JAR del proyecto al contenedor
COPY target/ProyectoClinica-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto en el que corre la aplicación
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
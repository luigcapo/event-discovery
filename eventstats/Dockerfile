# ÉTAPE 1: CONSTRUCTION (BUILD)
# Utilise une image Maven avec une version récente de JDK (ici 23, car la 24 n'est pas encore en version stable pour Maven)
FROM maven:3.9.8-eclipse-temurin-21 AS builder

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests


# ÉTAPE 2: EXÉCUTION (RUN)
# Utilise une image JRE 24 légère
# Note: Au moment de la rédaction, les images Temurin 24 ne sont pas encore taguées 'latest' ou 'stable'.
# Il est possible que vous deviez ajuster ce tag à l'avenir.
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Aucune exposition de port n'est nécessaire car ce sont des consommateurs Kafka.

# Copie le JAR depuis l'étape de build
COPY --from=builder /app/target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]
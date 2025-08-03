# ÉTAPE 1: CONSTRUCTION (BUILD)
# Utilise une image officielle Maven avec Java 21 (Temurin) pour construire l'artefact.
# L'alias 'builder' permet de référencer cette étape plus tard.
FROM maven:3.9.6-eclipse-temurin-21 AS builder

# Définit le répertoire de travail dans le conteneur.
WORKDIR /app

# Copie d'abord le pom.xml pour tirer parti du cache de Docker.
# Les dépendances ne seront téléchargées à nouveau que si le pom.xml change.
COPY pom.xml .
RUN mvn dependency:go-offline

# Copie le reste du code source de l'application.
COPY src ./src

# Compile l'application et crée le fichier JAR.
RUN mvn package -DskipTests

# ÉTAPE 2: EXÉCUTION (RUN)
# Utilise une image JRE (Java Runtime Environment) beaucoup plus légère pour l'exécution.
# C'est suffisant pour faire tourner l'application et réduit la taille de l'image finale.
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
EXPOSE 8080
# Copie uniquement le fichier JAR de l'étape de construction ('builder') vers l'image finale.
# Cela permet de garder l'image finale petite et propre, sans le code source ni les outils de build.
COPY --from=builder /app/target/*.jar app.jar
# Commande pour démarrer l'application lorsque le conteneur est lancé.
CMD ["java", "-jar", "app.jar"]
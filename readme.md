## GreenThumb
Api rest du projet backend du site GreenThum

## Description
Cette application communique avec une base de donnée postgreSQl pour retourner les données à afficher sur le site et
réaliser les actions de l'utilisateur

## Architecture
- Spring boot 3.5.6
- Java 25
- PostgreSQL 
- Gradle
- Rest

## Installation et Execution
1. Cloner le répository
2. Configurer la base de donnée (installer postgreSql et crée une base de donnée GreenThumb)
3. lancer ./Gradlew bootrun (il lance les migrations, les seeders et lance le back)
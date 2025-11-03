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
3. lancer ./gradlew bootRun (il lance les migrations, les seeders et lance le back) => lance en dev

Commande possible : 

    - ./gradlew bootRunProd
    - ./gradlew bootRunStanging

### Installer PostgreSQL
1. Installer postgreSQL : https://www.postgresql.org/download/ (version 18)
2. Quand il affiche la fenetre d'execution de stack builder, fermez-là
3. Ouvrer pgAdmin4 et ajouter une une base de donnée GreenThumb
4. Configurer la bd sur intelliJ pour avoir accés depuis l'ide
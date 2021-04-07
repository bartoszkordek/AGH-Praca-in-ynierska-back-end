[![Back-end CI](https://github.com/bartoszkordek/AGH-Praca-inzynierska-back-end/actions/workflows/back-end-ci.yml/badge.svg)](https://github.com/bartoszkordek/AGH-Praca-inzynierska-back-end/actions/workflows/back-end-ci.yml)
![GitHub last commit](https://img.shields.io/github/last-commit/bartoszkordek/AGH-Praca-inzynierska-back-end)
![GitHub contributors](https://img.shields.io/github/contributors/bartoszkordek/AGH-Praca-inzynierska-back-end)

### [English version below](#en-agh-praca-inzynierska-back-end)

# AGH-Praca-inżynierska-back-end
Praca inżynierska "System do wspomagania zarządzania placówką profilaktyki zdrowotnej" autorstwa Bartosza Kordka i Grzegorza Zacharskiego.

## Back-end
Aplikacja zrealizowana w architekturze mikroserwisowej oraz napisana w oparciu o framework Spring.

Serwisy:
* gateway
* discovery
* user
* trainings

## Jak uruchomić?
1. Wymagany jest zainstalowany [Docker](https://www.docker.com/).
1. Sklonuj projekt.
1. W głównym folderze projektu wpisz w terminalu:
    ```shell script
    docker-compose up --build -d
    ```
1. Uruchomienie może zająć do kilku minut.
1. Ostatecznie będą dostępne punkty końcowe:
    * http://localhost:8010 - panel administracyjny serwisu discovery
    * http://localhost:8020 - brama wejściowa do której kierowane są zapytania

## Jak zatrzymać?
1. W głównym folderze projektu wpisz w terminalu:
    ```shell script
    docker-compose down
    ```

# [EN AGH-Praca-inzynierska-back-end](#en-agh-praca-inzynierska-back-end)
Engineering thesis "System to support management of a health prevention unit" by Bartosz Kordek & Grzegorz Zacharski.

## Back-end
Microservices written in Spring Boot:
* gateway
* discovery
* user
* trainings

## How to start?
1. You will need [Docker](https://www.docker.com/).
1. Clone the project.
1. In root folder of the project type in terminal:
    ```shell script
    docker-compose up --build -d
    ```
1. Startup may last up to a few minutes.
1. Eventually there will be available pages/endpoints:
    * http://localhost:8010 - discovery service dashboard
    * http://localhost:8020 - api gateway

## How to stop?
1. In root folder of the project type in terminal:
    ```shell script
    docker-compose down
    ```

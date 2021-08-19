[![Continuous Delivery](https://github.com/bartoszkordek/AGH-Praca-inzynierska-back-end/actions/workflows/continuous-delivery.yml/badge.svg)](https://github.com/bartoszkordek/AGH-Praca-inzynierska-back-end/actions/workflows/continuous-delivery.yml)
[![Continuous Integration](https://github.com/bartoszkordek/AGH-Praca-inzynierska-back-end/actions/workflows/continuous-integration.yml/badge.svg)](https://github.com/bartoszkordek/AGH-Praca-inzynierska-back-end/actions/workflows/continuous-integration.yml)
![GitHub last commit](https://img.shields.io/github/last-commit/bartoszkordek/AGH-Praca-inzynierska-back-end)
![GitHub contributors](https://img.shields.io/github/contributors/bartoszkordek/AGH-Praca-inzynierska-back-end)

### [English version below](#en-agh-praca-inzynierska-back-end)

# AGH-Praca-inżynierska-back-end

Praca inżynierska "System do wspomagania zarządzania placówką profilaktyki zdrowotnej" autorstwa Bartosza Kordka i
Grzegorza Zacharskiego.

## Back-end

Aplikacja zrealizowana w architekturze mikroserwisowej oraz napisana w oparciu o framework Spring.

Serwisy:

* gateway
* discovery
* config-server
* account
* auth
* gympass
* task
* trainings

## Jak uruchomić? (Wersja Ubuntu)

1. Wymagany jest zainstalowany Docker:
    * [Docker Engine](https://docs.docker.com/engine/install/)
    * oraz [Docker Compose](https://docs.docker.com/compose/install/).
2. Aby pominąć punkty od 3 do 6 można wykorzystać skrypt.
3. Wpisz następujące komendy, aby utwórzyć następującą strukturę folderów na dysku:
   ```shell script
    cd 
    mkdir -p app/front-end
    mkdir -p app/back-end/properties
    ```
4. Do folderu __~/app/front-end__ skopiuj plik [__docker-compose-front.yaml__](https://github.com/bartoszkordek/AGH-Praca-inzynierska-back-end/blob/main/docker-compose-front.yaml).
5. Skopiuj plik [__docker-compose.yaml__](https://github.com/bartoszkordek/AGH-Praca-inzynierska-back-end/blob/main/docker-compose.yaml) oraz [__application-example.properties__](https://github.com/bartoszkordek/AGH-Praca-inzynierska-back-end/blob/main/properties/application-example.properties)
   odpowiednio do folderów __~/app/back-end__ oraz __~/app/back-end/properties__.
6. Zmień nazwę pliku z __application-example.properties__ na __application.properties__.
7. W pliku __application.properties__ zmodyfikuj odpowiednie właściwości.
   (Zalecana jest skrzynka pocztowa Gmail. Należy włączyć
   na [dostęp do mniej bezpiecznych aplikacji](https://support.google.com/accounts/answer/6010255?hl=pl#zippy=%2Cje%C5%9Bli-na-koncie-jest-w%C5%82%C4%85czony-dost%C4%99p-mniej-bezpiecznych-aplikacji)
   .)
8. W folderze __~/app/front-end__ należy wpisać komendę:
   ```shell script
    docker-compose -f docker-compose-front.yaml -d up 
    ```
9. W folderze __~/app/back-end__ należy wpisać komendę:
    ```shell script
    docker swarm init --listen-addr 0.0.0.0
    docker stack deploy --compose-file docker-compose.yaml backend
    ```
10. Uruchomienie może zająć do kilku minut. Uruchom przeglądarkę oraz wejść na stronę: __http://localhost:8010__. W
    panelu powinny znajdować wszystkie uruchomione serwisy (account,auth,config-server,gateway,gympass, task oraz
    trainings). W terminalu można też wpisać następującą komendę, aby sprawdzić stan mikroserwisów:
    ```shell script
    docker stack services backend
    ```
11. Po uruchomieniu, w terminalu należy wpisać następującą komendę, aby ostatecznie skonfigurować mikroserwisy.
    ```shell script
    curl -v -X POST http://localhost:8030/actuator/bus-refresh
    ```
    Poprawną odpowiedzią jest odpowiedź ze statusem 204. W celu ostatecznego sprawdzenia należy wykonać następujące
    komendy:
    ```shell script
    curl -X GET http://localhost:8020/auth/actuator/env > auth.json
    curl -X GET http://localhost:8020/trainings/actuator/env > trainings.json
    firefox auth.json trainings.json
    ```
    W przeglądarce należy wcisnąć CTRL+F, a następnie wpisać __spring.mail.username__ oraz __spring.mail.password__. 
    Jeżeli podany adres email się zgadza z podanym w pliku application.properties, to oznacza, że serwisy zostały poprawnie skonfigurowane. 
    W przypadku __spring.mail.password__ powinny zostać wyświetlone "******".
12. Ostatecznie będą dostępne punkty końcowe:
    * http://localhost:8010 - panel administracyjny serwisu discovery
    * http://localhost:8020 - brama wejściowa do której kierowane są zapytania
    * http://localhost:8030 - serwis konfiguracyjny
    * http://localhost:15672 - panel administracyjny rabbitmq (użytkownik: healthy_gym, hasło:
      th1sPAsswordNeed2BeChAnged)
    * port :27017 - baza mongodb (baza: databasePracaInz, użytkownik: adminPracaInz, hasło: thisPAsswordNeed2BeChange)
    * port :6379 - baza redis (hasło: thisP@sswordNeed2BeChange)
    * http://localhost:5000 - graficzny interfejs użytkownika

## Jak zatrzymać?

1. W folderze __~/app/front-end__ należy wpisać komendę:
   ```shell script
    docker-compose -f docker-compose-front.yaml down
    ```
2. W folderze __~/app/back-end__ należy wpisać komendę:
    ```shell script
    docker stack rm backend
    docker swarm leave
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

1. Open terminal in root folder of project.
2. Run the following commands:
 * If you are using ___Linux___ then use this command::
    ```shell
        docker build -f ./logstash/Dockerfile -t logstash .
    ```
    ```shell
        docker run -dp 9600:9600 \
                   -v "$(pwd)/logs/:/logs/" \
                   -v "$(pwd)/logstash/config/:/usr/share/logstash/pipeline/" \
                   logstash
    ```
 * If you are using ___PowerShell___ then use this command:
    ```shell
       docker build -f ./logstash/Dockerfile -t logstash .
    ```
    ```shell
        docker run -dp 9600:9600 `
                   -v "$(pwd)/logstash/config/:/usr/share/logstash/pipeline/" `
                   -v "$(pwd)/logs/:/logs/" `
                   docker.elastic.co/logstash/logstash:7.10.1
    ```
    ```shell
        docker run -dp 9600:9600 -v "$(pwd)/logs/:/logs/" logstash
    ```
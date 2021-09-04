1. Open terminal in root folder of project.
2. Run the following commands:
* If you are using ___Linux___ then use this command::
   ```shell
       docker run -d \
              -p 9200:9200 \
              -p 9300:9300 \
              -v "$(pwd)/logs/:/usr/share/elasticsearch/data" \
              -e "discovery.type=single-node"
              elasticsearch:7.14.0
   ```
* If you are using ___PowerShell___ then use this command:
   ```shell
       docker run -d `
                  -p 9200:9200 `
                  -p 9300:9300 `
                  -v "$(pwd)/logs/:/usr/share/elasticsearch/data" `
                  -e "discovery.type=single-node" `
                   docker.elastic.co/elasticsearch/elasticsearch:7.10.1
   ```
     ```shell
       docker run -d `
                  -p 9200:9200 `
                  -p 9300:9300 `
                  -e "discovery.type=single-node" `
                  -v "$(pwd)/logs/:/usr/share/elasticsearch/data" `
                  docker.elastic.co/elasticsearch/elasticsearch:7.10.1
   ```
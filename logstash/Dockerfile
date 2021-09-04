FROM docker.elastic.co/logstash/logstash:7.10.1
RUN rm -f /usr/share/logstash/pipeline/logstash.conf
ADD pipeline/ /usr/share/logstash/pipeline/

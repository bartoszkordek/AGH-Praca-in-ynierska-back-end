FROM logstash:7.14.0
RUN rm -f /usr/share/logstash/pipeline/logstash-app.conf
ADD ./logs/ /logs/
ADD ./logstash/config/ /usr/share/logstash/pipeline/

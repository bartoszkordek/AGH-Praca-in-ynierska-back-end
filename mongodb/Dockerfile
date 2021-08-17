FROM mongo:4.4.4-bionic
MAINTAINER g.zacharski
ENV MONGO_INITDB_DATABASE=databasePracaInz
ENV MONGO_INITDB_ROOT_USERNAME=adminPracaInz
ENV MONGO_INITDB_ROOT_PASSWORD=thisPAsswordNeed2BeChange
COPY dump /home/dump
COPY mongo-init.js mongo-restore.sh ./docker-entrypoint-initdb.d/
CMD ["mongod"]
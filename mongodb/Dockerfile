FROM mongo:4.4.4-bionic
MAINTAINER g.zacharski
ENV MONGO_INITDB_DATABASE=databasePracaInz
ENV MONGO_INITDB_ROOT_USERNAME=adminPracaInz
ENV MONGO_INITDB_ROOT_PASSWORD=thisP@sswordNeed2BeChange
COPY mongo-init.js ./docker-entrypoint-initdb.d/mongo-init.js
CMD ["mongod"]
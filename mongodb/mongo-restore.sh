#!/bin bash
# Init or restore from dump
mongorestore --drop --gzip -d=databasePracaInz \
  -u=adminPracaInz -p=thisPAsswordNeed2BeChange \
  --archive=/home/dump/initial.data.gz

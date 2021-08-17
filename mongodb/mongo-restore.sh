#!/bin/bash
# Init or restore from dump
mongorestore --drop --gzip --db=databasePracaInz \
  --username=adminPracaInz --password=thisPAsswordNeed2BeChange \
  --archive=/home/dump/initial.data.gz

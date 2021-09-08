* If you are using ___Linux___ then use this command::
```shell script
    mongodump \
      --uri="mongodb://adminPracaInz:thisPAsswordNeed2BeChange@localhost:27017/databasePracaInz" \
      --gzip \
      --archive=initial.data.gz
```
* If you are using ___PowerShell___ then use this command:
```shell script
    mongodump `
      --uri="mongodb://adminPracaInz:thisPAsswordNeed2BeChange@localhost:27017/databasePracaInz" `
      --gzip `
      --archive=initial.data.gz
```
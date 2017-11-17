# sql2mongo

A tool designed to load basic SQL Insert statements into MongoDB.

Can be run directly against a running MongoDB instance or write the output to a json file which can then be imported into MongoDB using mongoimport.

## Usage
```
 -d,--database <arg>     database
 -h,--host <arg>         MongoDB host
 -help,--help            Show Help
 -i,--input <arg>        SQL input file
 -p,--port <arg>         MongoDB port
 -pwd,--password <arg>   MongoDB password
 -u,--user <arg>         MongoDB username
```

example :
```
java -jar sql2mongo.jar --host 127.0.0.1 --port 27017 --database test --input test.sql
```

if the host parameter is not set, the output will be written as json to a file with the same name as the input with a .json extension.

## Notes

* unparsable lines will be ignored
* complex datatypes or use of SQL functions within an insert statement are not supported (e.g. to_date)
* supports string, boolean, integer and float datatypes
* if you are using authentication, the authentication database is assumed to be 'admin'
* output is written to MongoDB in batches of 1000 documents
* data is streamed from input to output (rather than reading everything into memory before writing)
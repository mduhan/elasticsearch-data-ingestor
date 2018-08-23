# Elasticsearch Data Ingestor upload data in Elasticsearch from csv and from json

## What you'll need
- JDK 1.8+
- Gradle 4.1
- Elasticsearch 6+

## Stack
- Java
- Spring Boot
- FreeMarker
- JavaScript

## Build
   gradle clean build
## Run
`  java -jar build/libs/elasticsearch-data-ingestor-1.0.jar

## Build
`  java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=n -jar build/libs/elasticsearch-data-ingestor-1.0.jar

## Access
 http://localhost:9090/
 
## Help

Please verify elasticsearch cluster name in form
 
sample es schema mapping file :-   https://github.com/mduhan/elasticsearch-data-ingestor/blob/master/src/main/resources/sample-mapping.json
 

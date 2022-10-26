### worst-movie
A microservice that gives metrics about Golden Raspberry Awards.

### How to build
Simply run:

```./mvnw clean install```

### How to run the application

```java -jar target/WorstMovie-0.0.1-SNAPSHOT.jar --csv=src/main/resources/movielist.csv```

The ```--csv``` parameter is optional. You can give a CSV file to populate the database on 
application startup.

### TODO
- Extract domain classes to another module
- Create @ControllerAdvice / exception handler
- [DONE] Accept csv file as a command line parameter
- Create integration test for business feature
- [DONE] Add instructions to build and run application
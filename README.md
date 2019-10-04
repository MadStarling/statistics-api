# Statistics-api

## Building the project

To build the project, simply execute  
Linux:    ```./gradlew build  ```
Windows:    ```gradlew.bat build  ```

## Executing the tests

The tests will be executed everytime you build the app, but to execute solely the tests run  
Linux:    ```./gradlew test  ```
Windows:    ```gradlew.bat test  ```

## Running the application

There are two ways of running this application:

### Direct execution

After building the application, execute the following command inside the build/libs folder:

```java -jar statistics-api.jar```

### Gradle execution

If you choose to use gradle, execute 

Linux:    ```./gradlew run  ```
Windows:    ```gradlew.bat run  ```

### Docker execution

If you choose to use docker, simply execute

```docker-compose up```

and the application will be built and started.
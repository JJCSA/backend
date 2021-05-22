# JJCSA Backend Spring Boot Application

## Contributing:

For steps on how you can contribute, please follow the [Contributing guide](CONTRIBUTING.md)

## Resources:

For resources on various documentation and references, please refer [Resources](RESOURCES.md)

## Running the app:

1. Clone this repository and navigate to the backend directory

    ```
    git clone git@github.com:JJCSA/backend.git
    cd backend
    ```

2. Install Postgres in local machine  
  
        For Windows, Follow these instructions: https://www.2ndquadrant.com/en/blog/pginstaller-install-postgresql/
  
        For Mac, Follow these instructions: https://www.robinwieruch.de/postgres-sql-macos-setup
  
        For Ubuntu, Follow these instructions: https://phoenixnap.com/kb/how-to-install-postgresql-on-ubuntu

3. In Postgres, create a new database called `jjcsa` and a role (user) with the name `admin` with password `jjcsa`
        If not, create the same with the following steps:  
        
        # Open a psql terminal
        sudo -u postgres psql

        # Execute the following psql queries
        postgres=# create database jjcsa;
        postgres=# create user admin with encrypted password 'jjcsa';
        postgres=# grant all privileges on database jjcsa to admin;

4. Make sure Keycloak service is already running. If not, start the same with the following instructions: https://github.com/JJCSA/Authentication/tree/developer#steps-to-start-the-server  

5. Now navigate to the backend folder and run 
    ```
    mvn clean spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=9080" -Dspring-boot.run.profiles=dev
    ```
    This will start you application server on port 9080.  
    
    If running the app in IntelliJ, make sure to add profiles to the Run Configuration:
    ![IntelliJ Config](IntelliJ-Config.png)
    
    If the app is run in a docker container, add the env var for spring profile:
    ```
    docker run -e "SPRING_PROFILES_ACTIVE=stage" <IMAGE>
    ```
    
6. Now you can test the APIs using postman or any other external client. For the APIs that need authentication use the token returned by the login API. The authorization type would be Bearer token.
   
   Eg API: http://localhost:9080/api/users/login

7. You can test the App heath status by going to http://localhost:9080/actuator/health

## Documentation

The app uses [springdoc-openapi](https://springdoc.org) for API Documentation. 
To access the API documentation, access Swagger UI at [http://localhost:9080/swagger-ui.html](http://localhost:9080/swagger-ui.html)

# JJCSA Backend Spring Boot Application

## Contributing:

For steps on how you can contribute, please follow the [Contributing guide](CONTRIBUTING.md)

## Resources:

For resources on various documentation and references, please refer [Resources](RESOURCES.md)

## Development Instructions:

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

4. Install the Maven dependencies for the app by running the command
    ```
    mvn clean install
    ```

5. Build the project using maven
    ```
    mvn package
    ```
This will create all the project deliverables in the `target` directory

#### Running the app:

1. Install Keycloak from this link https://www.keycloak.org/getting-started/getting-started-zip and unzip it to the desired location

2. Navigate to Keycloak bin folder and run ./standalone.sh. This will start the Keycloak server and you can view the Keycloak UI on http://localhost:8080/auth.
To login to the Keycloak server credentails would be 
    ```
   username: admin
   password: password
   ```
    
3. Start the postgres server using 
    ```
    pg_ctl -D /usr/local/var/postgres start
    ```
4. Now navigate to the backend folder and run 
    ```
    mvn clean spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=9080"
    ```
    This will start you application server on port 9080.
    
    Once the startup finishes, KeycloakInitializerRunner.java class will run and initialize jjcsa-services realm in Keycloak. Basically, it will create:
    ```
    Realm: jjcsa-services
    Client: jjcsa
    Client Roles: ADMIN and USER
    Two users
    admin: with roles ADMIN and USER
    user: only with role USER
    ```
    
5. Now you can test the APIs using postman or any other external client. For the APIs that need authentication use the token returned by the login API. The authorization type would be Bearer token.
   
   Eg API: http://localhost:9080/api/users/login
   

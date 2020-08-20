# JJCSA Backend Spring Boot Application

## Contributing:

For steps on how you can contribute, please follow the [Contributing guide](CONTRIBUTING.md)

## Development Instructions:

1. Clone this repository and navigate to the frontend directory

```
git clone git@github.com:JJCSA/backend.git
cd frontend
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


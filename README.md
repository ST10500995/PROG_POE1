# PROG POE 1

Part One of the Programming 1A Portfolio of Evidence: a console-based chat app registration and login feature.

## Requirements

- Java 26
- Maven

## Run the application

```sh
mvn package
java -cp target/classes com.mycompany.prog_poe.PROG_POE
```

The application asks for a first and last name, then validates a username, password, and South African cell number in international format before prompting for login details.

## Run the unit tests

```sh
mvn test
```

GitHub Actions runs the Maven tests on pushes and pull requests.

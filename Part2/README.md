# PROG POE Part 2 — Sending Messages

Part Two of the Programming 1A Portfolio of Evidence. This console-based QuickChat application builds on the registration and login feature from Part One and adds message creation, validation, tracking, sending, discarding, and JSON storage.

## Requirements

- Java 17 or later
- Maven

## Run the application

```sh
mvn package
java -cp target/classes com.mycompany.prog_poe.PROG_POE
```

Register with a first and last name, a valid username and password, and a South African cell number in international format. After a successful login, choose the number of messages and use the menu to send them, view recent message activity, or quit.

Messages can be sent, disregarded, or stored in `stored_messages.json` in the working directory.

## Run the unit tests

```sh
mvn test
```

GitHub Actions runs the Maven unit tests for both Part One and Part Two on pushes and pull requests using the repository workflow at `.github/workflows/test-java.yml`.

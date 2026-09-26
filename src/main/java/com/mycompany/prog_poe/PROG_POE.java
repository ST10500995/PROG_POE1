package com.mycompany.prog_poe;

import java.util.Scanner;

/** Console entry point for Part One: registration and login. */
public class PROG_POE {

    private static final String USERNAME_SUCCESS = "Username successfully captured.";
    private static final String USERNAME_FAILURE = "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
    private static final String PASSWORD_SUCCESS = "Password successfully captured.";
    private static final String PASSWORD_FAILURE = "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
    private static final String CELL_SUCCESS = "Cell number successfully captured.";
    private static final String CELL_FAILURE = "Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.";

    private PROG_POE() {
        // This class is only the console application's entry point.
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("=== Chat App: Registration and Login ===");
            String firstName = readRequired(scanner, "First name: ");
            String lastName = readRequired(scanner, "Last name: ");
            Login login = new Login(firstName, lastName);

            System.out.print("Choose a username (include _ and use at most 5 characters): ");
            String username = scanner.nextLine();
            boolean usernameValid = login.checkUserName(username);
            System.out.println(usernameValid ? USERNAME_SUCCESS : USERNAME_FAILURE);

            System.out.print("Choose a password (8+ chars, uppercase, number, special character): ");
            String password = scanner.nextLine();
            boolean passwordValid = login.checkPasswordComplexity(password);
            System.out.println(passwordValid ? PASSWORD_SUCCESS : PASSWORD_FAILURE);

            System.out.print("South African cell number in international format (e.g. +27838968976): ");
            String cellPhoneNumber = scanner.nextLine();
            boolean cellPhoneValid = login.checkCellPhoneNumber(cellPhoneNumber);
            System.out.println(cellPhoneValid ? CELL_SUCCESS : CELL_FAILURE);

            if (!(usernameValid && passwordValid && cellPhoneValid)) {
                System.out.println("Registration could not be completed. Please restart and correct the invalid details.");
                return;
            }

            System.out.println(login.registerUser(username, password, cellPhoneNumber));
            System.out.println();
            System.out.println("=== Login ===");
            System.out.print("Username: ");
            String enteredUsername = scanner.nextLine();
            System.out.print("Password: ");
            String enteredPassword = scanner.nextLine();
            boolean loginSuccessful = login.loginUser(enteredUsername, enteredPassword);
            System.out.println(login.returnLoginStatus(loginSuccessful));
        }
    }

    private static String readRequired(Scanner scanner, String prompt) {
        String value;
        do {
            System.out.print(prompt);
            value = scanner.nextLine().trim();
            if (value.isEmpty()) {
                System.out.println("This field is required.");
            }
        } while (value.isEmpty());
        return value;
    }
}

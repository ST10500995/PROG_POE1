package com.mycompany.prog_poe;

import java.util.regex.Pattern;

/** Holds the Part One registration and login checks for the chat application. */
public class Login {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^(?=.*_).{1,5}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9\\s])\\S{8,}$");

    /*
     * ICASA explains that South African telephone numbers are ten digits in
     * national format. In international format, +27 replaces the national
     * trunk prefix 0, leaving nine subscriber digits after the country code.
     * This regex checks that country code and length for the assignment's
     * international-format cell number requirement.
     * Source: Independent Communications Authority of South Africa (ICASA),
     * “Numbering”, https://www.icasa.org.za/pages/numbering (accessed 26 Sep 2026).
     */
    private static final Pattern CELL_PHONE_PATTERN = Pattern.compile("^\\+27\\d{9}$");

    private final String firstName;
    private final String lastName;
    private String username;
    private String password;
    private String cellPhoneNumber;
    private boolean registered;
    private boolean lastLoginSuccessful;

    public Login() {
        this("", "");
    }

    public Login(String firstName, String lastName) {
        this.firstName = firstName == null ? "" : firstName.trim();
        this.lastName = lastName == null ? "" : lastName.trim();
    }

    public boolean checkUserName(String candidate) {
        return candidate != null && USERNAME_PATTERN.matcher(candidate).matches();
    }

    public boolean checkUserName() {
        return checkUserName(username);
    }

    public boolean checkPasswordComplexity(String candidate) {
        return candidate != null && PASSWORD_PATTERN.matcher(candidate).matches();
    }

    public boolean checkPasswordComplexity() {
        return checkPasswordComplexity(password);
    }

    public boolean checkCellPhoneNumber(String candidate) {
        return candidate != null && CELL_PHONE_PATTERN.matcher(candidate).matches();
    }

    public boolean checkCellPhoneNumber() {
        return checkCellPhoneNumber(cellPhoneNumber);
    }

    /** Validates supplied details and stores them only when every check passes. */
    public String registerUser(String newUsername, String newPassword, String newCellPhoneNumber) {
        username = newUsername;
        password = newPassword;
        cellPhoneNumber = newCellPhoneNumber;
        registered = false;

        if (!checkUserName()) {
            return "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
        }
        if (!checkPasswordComplexity()) {
            return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        }
        if (!checkCellPhoneNumber()) {
            return "Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.";
        }

        registered = true;
        return "Registration successful.";
    }

    public String registerUser() {
        return registerUser(username, password, cellPhoneNumber);
    }

    public boolean loginUser(String enteredUsername, String enteredPassword) {
        lastLoginSuccessful = registered
                && username.equals(enteredUsername)
                && password.equals(enteredPassword);
        return lastLoginSuccessful;
    }

    public boolean loginUser() {
        return loginUser(username, password);
    }

    public String returnLoginStatus() {
        return returnLoginStatus(lastLoginSuccessful);
    }

    public String returnLoginStatus(boolean loginSuccessful) {
        if (!loginSuccessful) {
            return "Username or password incorrect, please try again.";
        }
        return "Welcome " + firstName + ", " + lastName + " it is great to see you again.";
    }

    public String getUsername() {
        return username;
    }

    public String getCellPhoneNumber() {
        return cellPhoneNumber;
    }
}

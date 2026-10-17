package com.mycompany.prog_poe;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/** Console entry point for QuickChat (Part One registration/login and Part Two messaging). */
public class PROG_POE {
    private static final String USERNAME_SUCCESS = "Username successfully captured.";
    private static final String USERNAME_FAILURE = "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
    private static final String PASSWORD_SUCCESS = "Password successfully captured.";
    private static final String PASSWORD_FAILURE = "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
    private static final String CELL_SUCCESS = "Cell number successfully captured.";
    private static final String CELL_FAILURE = "Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.";
    private static final Path STORED_MESSAGES_FILE = Path.of("stored_messages.json");

    private PROG_POE() { }

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
            System.out.println("\n=== Login ===");
            System.out.print("Username: ");
            String enteredUsername = scanner.nextLine();
            System.out.print("Password: ");
            String enteredPassword = scanner.nextLine();
            boolean loginSuccessful = login.loginUser(enteredUsername, enteredPassword);
            System.out.println(login.returnLoginStatus(loginSuccessful));
            if (loginSuccessful) runQuickChat(scanner);
        }
    }

    private static void runQuickChat(Scanner scanner) {
        System.out.println("\nWelcome to QuickChat.");
        List<Message> messages = new ArrayList<>();
        int totalMessages = readPositiveInt(scanner, "How many messages would you like to enter? ");
        int sentCount = 0;
        boolean running = true;
        while (running) {
            System.out.println("\n1) Send Messages\n2) Show recently sent messages\n3) Quit");
            int option = readInt(scanner, "Choose an option: ");
            switch (option) {
                case 1:
                    if (messages.size() >= totalMessages) {
                        System.out.println("You have reached the number of messages selected.");
                        break;
                    }
                    while (messages.size() < totalMessages) {
                        int messageNumber = messages.size();
                        System.out.println("\nMessage " + (messageNumber + 1) + " of " + totalMessages);
                        System.out.print("Recipient (+27 followed by 9 digits): ");
                        String recipient = scanner.nextLine().trim();
                        System.out.print("Message (maximum 250 characters): ");
                        String body = scanner.nextLine();
                        Message message = new Message(messageNumber, recipient, body);
                        System.out.println(message.checkRecipientCell());
                        System.out.println(message.validateMessage());
                        if (!message.isRecipientValid() || !message.isMessageValid()) {
                            System.out.println("Message was not processed. Please select Send Messages to try again.");
                            break;
                        }
                        System.out.println("Message ID generated: " + message.getMessageId());
                        System.out.println("Message Hash: " + message.getMessageHash());
                        System.out.println("Choose: 1) Send Message  2) Disregard Message  3) Store Message");
                        int action = readInt(scanner, "Your choice: ");
                        if (action == 2) {
                            System.out.print("Enter 0 to delete/disregard this message: ");
                            String confirmation = scanner.nextLine().trim();
                            if (!"0".equals(confirmation)) System.out.println("Message kept as disregarded.");
                        }
                        if (action == 3) {
                            try { message.storeMessage(STORED_MESSAGES_FILE); }
                            catch (IOException ex) { System.out.println("Could not store message: " + ex.getMessage()); }
                        }
                        System.out.println(message.SentMessage(action));
                        System.out.println(message.printMessages());
                        messages.add(message);
                        sentCount += message.returnTotalMessagess();
                    }
                    System.out.println("Total messages sent: " + sentCount);
                    break;
                case 2:
                    System.out.println("Coming Soon.");
                    break;
                case 3:
                    running = false;
                    System.out.println("Thank you for using QuickChat. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option. Please select 1, 2 or 3.");
            }
        }
    }

    private static String readRequired(Scanner scanner, String prompt) {
        String value;
        do {
            System.out.print(prompt);
            value = scanner.nextLine().trim();
            if (value.isEmpty()) System.out.println("This field is required.");
        } while (value.isEmpty());
        return value;
    }

    private static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try { return Integer.parseInt(input); }
            catch (NumberFormatException ex) { System.out.println("Please enter a numeric option."); }
        }
    }

    private static int readPositiveInt(Scanner scanner, String prompt) {
        int value;
        do {
            value = readInt(scanner, prompt);
            if (value < 1) System.out.println("Enter at least 1 message.");
        } while (value < 1);
        return value;
    }
}

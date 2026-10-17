package com.mycompany.prog_poe;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.regex.Pattern;

/** Represents a QuickChat message and its generated tracking details. */
public class Message {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Pattern RECIPIENT_PATTERN = Pattern.compile("^\\+27\\d{9}$");
    private final String messageId;
    private final int messageNumber;
    private final String recipient;
    private final String message;
    private final String messageHash;
    private String status;

    public Message(int messageNumber, String recipient, String message) {
        this.messageNumber = messageNumber;
        this.messageId = generateMessageId();
        this.recipient = recipient == null ? "" : recipient.trim();
        this.message = message == null ? "" : message.trim();
        this.messageHash = createMessageHash();
        this.status = "Pending";
    }

    private static String generateMessageId() {
        long number = Math.abs(RANDOM.nextLong() % 10_000_000_000L);
        return String.format(Locale.ROOT, "%010d", number);
    }

    public boolean checkMessageID() { return messageId.length() <= 10; }

    public String checkRecipientCell() {
        return RECIPIENT_PATTERN.matcher(recipient).matches()
                ? "Cell phone number successfully captured."
                : "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
    }

    public boolean isRecipientValid() { return RECIPIENT_PATTERN.matcher(recipient).matches(); }

    public String validateMessage() {
        if (message.length() > 250) {
            return "Message exceeds 250 characters by " + (message.length() - 250) + "; please reduce the size.";
        }
        return "Message ready to send.";
    }

    public boolean isMessageValid() { return message.length() <= 250 && !message.isEmpty(); }

    public String createMessageHash() {
        String[] words = message.trim().split("\\s+");
        String first = words.length == 0 || words[0].isEmpty() ? "" : words[0].replaceAll("^[^\\p{Alnum}]+|[^\\p{Alnum}]+$", "");
        String last = words.length == 0 || words[0].isEmpty() ? "" : words[words.length - 1].replaceAll("^[^\\p{Alnum}]+|[^\\p{Alnum}]+$", "");
        String idPrefix = messageId.length() >= 2 ? messageId.substring(0, 2) : messageId;
        return (idPrefix + ":" + messageNumber + ":" + first + last).toUpperCase(Locale.ROOT);
    }

    public String SentMessage(int choice) {
        switch (choice) {
            case 1: status = "Sent"; return "Message successfully sent.";
            case 2: status = "Disregarded"; return "Press 0 to delete the message.";
            case 3: status = "Stored"; return "Message successfully stored.";
            default: return "Invalid message option.";
        }
    }

    /** Appends this message as a JSON object to the local JSON array file. */
    public void storeMessage(Path file) throws IOException {
        String jsonObject = "{\"messageId\":\"" + escape(messageId) + "\",\"messageNumber\":" + messageNumber
                + ",\"recipient\":\"" + escape(recipient) + "\",\"message\":\"" + escape(message)
                + "\",\"messageHash\":\"" + escape(messageHash) + "\",\"status\":\"Stored\"}";
        String existing = Files.exists(file) ? Files.readString(file, StandardCharsets.UTF_8).trim() : "";
        String result;
        if (existing.isEmpty() || existing.equals("[]")) result = "[\n  " + jsonObject + "\n]";
        else {
            String body = existing;
            if (body.endsWith("]")) body = body.substring(0, body.length() - 1).trim();
            if (body.endsWith("[")) result = "[\n  " + jsonObject + "\n]";
            else result = body + ",\n  " + jsonObject + "\n]";
        }
        Path parent = file.toAbsolutePath().getParent();
        if (parent != null) Files.createDirectories(parent);
        Files.writeString(file, result, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    public String printMessages() {
        return "Message ID: " + messageId + "\nMessage Hash: " + messageHash + "\nRecipient: " + recipient
                + "\nMessage: " + message + "\nStatus: " + status;
    }
    public int returnTotalMessagess() { return "Sent".equals(status) ? 1 : 0; }
    public String getMessageId() { return messageId; }
    public int getMessageNumber() { return messageNumber; }
    public String getRecipient() { return recipient; }
    public String getMessage() { return message; }
    public String getMessageHash() { return messageHash; }
    public String getStatus() { return status; }
}

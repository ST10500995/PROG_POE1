package com.mycompany.prog_poe;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.regex.Pattern;

/** Represents one QuickChat message and its generated tracking details. */
public class Message {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Pattern RECIPIENT_PATTERN = Pattern.compile("^\\+27\\d{9}$");
    private final String messageId;
    private final int messageNumber;
    private final String recipient;
    private final String message;
    private final String messageHash;
    private String status = "Pending";

    public Message(int messageNumber, String recipient, String message) {
        this(messageNumber, recipient, message, null);
    }

    /** Creates a message with an optional deterministic ID for loop-driven application use. */
    public Message(int messageNumber, String recipient, String message, String generatedId) {
        this.messageNumber = messageNumber;
        this.recipient = recipient == null ? "" : recipient.trim();
        this.message = message == null ? "" : message.trim();
        this.messageId = generatedId == null ? generateMessageId() : generatedId;
        this.messageHash = createMessageHash();
    }

    private static String generateMessageId() {
        return String.format(Locale.ROOT, "%010d", RANDOM.nextInt(1_000_000_000));
    }

    /** Builds a ten-digit ID using the required two-character prefix and one-based loop counter. */
    public static String generateMessageId(String recipient, int messageNumber) {
        String counter = String.format(Locale.ROOT, "%08d", messageNumber + 1);
        return ("00" + counter).substring(0, 10);
    }

    public boolean checkMessageID() { return messageId.length() <= 10; }

    public String checkRecipientCell() {
        return isRecipientValid() ? "Cell phone number successfully captured."
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
        String trimmed = message.trim();
        String[] words = trimmed.isEmpty() ? new String[0] : trimmed.split("\\s+");
        String first = words.length == 0 ? "" : cleanWord(words[0]);
        String last = words.length == 0 ? "" : cleanWord(words[words.length - 1]);
        String idPrefix = messageId.length() >= 2 ? messageId.substring(0, 2) : messageId;
        return (idPrefix + ":" + messageNumber + ":" + first + last).toUpperCase(Locale.ROOT);
    }

    private static String cleanWord(String word) {
        return word.replaceAll("^[^\\p{Alnum}]+|[^\\p{Alnum}]+$", "");
    }

    /** Applies the user's Send, Disregard, or Store choice and returns the required response. */
    public String SentMessage(int choice) {
        switch (choice) {
            case 1: status = "Sent"; return "Message successfully sent.";
            case 2: status = "Disregarded"; return "Press 0 to delete the message.";
            case 3: status = "Stored"; return "Message successfully stored.";
            default: return "Invalid message option.";
        }
    }

    /**
     * Writes a valid JSON array containing this message, preserving existing entries.
     * JSON structure and string escaping follow RFC 8259, sections 2 and 7:
     * https://www.rfc-editor.org/rfc/rfc8259.html
     */
    public void storeMessage(Path file) throws IOException {
        String existing = Files.exists(file) ? Files.readString(file, StandardCharsets.UTF_8).trim() : "";
        String object = "{\"messageId\":\"" + escape(messageId) + "\",\"messageNumber\":" + messageNumber
                + ",\"recipient\":\"" + escape(recipient) + "\",\"message\":\"" + escape(message)
                + "\",\"messageHash\":\"" + escape(messageHash) + "\",\"status\":\"Stored\"}";
        String result;
        if (existing.isEmpty() || "[]".equals(existing)) {
            result = "[\n  " + object + "\n]";
        } else if (existing.endsWith("]")) {
            String body = existing.substring(0, existing.length() - 1).trim();
            if (!body.startsWith("[")) throw new IOException("Stored message file is not a JSON array.");
            result = body.length() == 1 ? "[\n  " + object + "\n]" : body + ",\n  " + object + "\n]";
        } else {
            throw new IOException("Stored message file is not a complete JSON array.");
        }
        Path parent = file.toAbsolutePath().getParent();
        if (parent != null) Files.createDirectories(parent);
        Files.writeString(file, result, StandardCharsets.UTF_8);
    }

    private static String escape(String value) {
        StringBuilder escaped = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '"': escaped.append("\\\""); break;
                case '\\': escaped.append("\\\\"); break;
                case '\b': escaped.append("\\b"); break;
                case '\f': escaped.append("\\f"); break;
                case '\n': escaped.append("\\n"); break;
                case '\r': escaped.append("\\r"); break;
                case '\t': escaped.append("\\t"); break;
                default:
                    if (c < 0x20) escaped.append(String.format(Locale.ROOT, "\\u%04x", (int) c));
                    else escaped.append(c);
            }
        }
        return escaped.toString();
    }

    /** Returns message details in the order required by the assignment. */
    public String printMessages() {
        return "Message ID: " + messageId + "\nMessage Hash: " + messageHash + "\nRecipient: " + recipient
                + "\nMessage: " + message;
    }

    public int returnTotalMessagess() { return "Sent".equals(status) ? 1 : 0; }
    public String getMessageId() { return messageId; }
    public int getMessageNumber() { return messageNumber; }
    public String getRecipient() { return recipient; }
    public String getMessage() { return message; }
    public String getMessageHash() { return messageHash; }
    public String getStatus() { return status; }
}

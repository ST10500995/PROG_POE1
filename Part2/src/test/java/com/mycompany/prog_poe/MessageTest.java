package com.mycompany.prog_poe;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.nio.file.Files;
import static org.junit.jupiter.api.Assertions.*;

class MessageTest {
    @TempDir Path tempDir;

    @Test void messageIdIsTenDigitsOrLess() {
        Message message = new Message(0, "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        assertTrue(message.checkMessageID());
        assertEquals(10, message.getMessageId().length());
        assertTrue(message.getMessageId().matches("\\d{10}"));
    }

    @Test void recipientValidationAcceptsInternationalSouthAfricanNumber() {
        Message message = new Message(0, "+27718693002", "Hello");
        assertEquals("Cell phone number successfully captured.", message.checkRecipientCell());
    }

    @Test void recipientValidationRejectsNationalFormat() {
        Message message = new Message(0, "08575975889", "Hello");
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.", message.checkRecipientCell());
    }

    @Test void messageLengthBoundaryIsValidated() {
        assertEquals("Message ready to send.", new Message(0, "+27718693002", "a".repeat(250)).validateMessage());
        assertEquals("Message exceeds 250 characters by 1; please reduce the size.", new Message(0, "+27718693002", "a".repeat(251)).validateMessage());
    }

    @Test void hashUsesIdPrefixMessageNumberAndFirstAndLastWords() {
        Message message = new Message(0, "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        assertEquals(message.getMessageId().substring(0, 2) + ":0:HITONIGHT", message.getMessageHash());
    }

    @Test void sendDisregardAndStoreActionsReturnExpectedMessages() throws Exception {
        Message sent = new Message(0, "+27718693002", "Hello there");
        assertEquals("Message successfully sent.", sent.SentMessage(1));
        assertEquals(1, sent.returnTotalMessagess());
        Message disregarded = new Message(1, "+27718693002", "Hello there");
        assertEquals("Press 0 to delete the message.", disregarded.SentMessage(2));
        assertEquals(0, disregarded.returnTotalMessagess());
        Message stored = new Message(2, "+27718693002", "Hello there");
        Path json = tempDir.resolve("stored_messages.json");
        stored.storeMessage(json);
        assertTrue(Files.readString(json).contains("\"status\":\"Stored\""));
        assertEquals("Message successfully stored.", stored.SentMessage(3));
    }
}

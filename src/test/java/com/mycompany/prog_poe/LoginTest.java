package com.mycompany.prog_poe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class LoginTest {

    private final Login login = new Login("Kylian", "Mbappe");

    @Test
    void acceptsUsernameWithUnderscoreAndMaximumLength() {
        assertTrue(login.checkUserName("kyl_1"));
    }

    @Test
    void rejectsUsernameWithoutUnderscoreOrOverMaximumLength() {
        assertFalse(login.checkUserName("kyle!!!!!!!"));
        assertFalse(login.checkUserName("kyle_12"));
    }

    @Test
    void acceptsPasswordMeetingAllComplexityRules() {
        assertTrue(login.checkPasswordComplexity("Ch&&sec@ke99!"));
        assertEquals("Password successfully captured.", passwordStatus("Ch&&sec@ke99!"));
    }

    @Test
    void rejectsPasswordMissingComplexity() {
        assertFalse(login.checkPasswordComplexity("password"));
        assertEquals("Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.",
                passwordStatus("password"));
    }

    @Test
    void acceptsSouthAfricanInternationalCellNumber() {
        assertTrue(login.checkCellPhoneNumber("+27838968976"));
        assertEquals("Cell number successfully captured.", cellPhoneStatus("+27838968976"));
    }

    @Test
    void rejectsCellNumberWithoutInternationalFormat() {
        assertFalse(login.checkCellPhoneNumber("08966553"));
        assertEquals("Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.",
                cellPhoneStatus("08966553"));
    }

    @Test
    void registersAndAuthenticatesOnlyMatchingCredentials() {
        assertEquals("Registration successful.",
                login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976"));
        assertTrue(login.loginUser("kyl_1", "Ch&&sec@ke99!"));
        assertEquals("Welcome Kylian, Mbappe it is great to see you again.", login.returnLoginStatus());
        assertFalse(login.loginUser("kyl_1", "wrong"));
        assertEquals("Username or password incorrect, please try again.", login.returnLoginStatus());
    }

    @Test
    void registrationStopsAtFirstInvalidField() {
        assertEquals("Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.",
                login.registerUser("kyle!!!!!!!", "Ch&&sec@ke99!", "+27838968976"));
        assertEquals("Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.",
                login.registerUser("kyl_1", "password", "+27838968976"));
        assertEquals("Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.",
                login.registerUser("kyl_1", "Ch&&sec@ke99!", "08966553"));
        assertFalse(login.loginUser("kyl_1", "Ch&&sec@ke99!"));
    }

    private String passwordStatus(String password) {
        return login.checkPasswordComplexity(password)
                ? "Password successfully captured."
                : "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
    }

    private String cellPhoneStatus(String cellPhoneNumber) {
        return login.checkCellPhoneNumber(cellPhoneNumber)
                ? "Cell number successfully captured."
                : "Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.";
    }
}

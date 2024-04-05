package com.example.fire_and_based;

import org.junit.Test;
import static org.junit.Assert.*;

import com.google.zxing.WriterException;

public class QRCodeGeneratorTest {
    @Test
    public void testSaveQRToFile() {
        String data = "Test data";
        int height = 200;
        int width = 200;
        // Assuming that the method should not throw any exception
        QRCodeGenerator.saveQRToFile(data, height, width);
    }

    @Test
    public void testGetValidCharsNoReplace() {
        String input = "ABC123";
        assertEquals("Valid characters not as expected", input, QRCodeGenerator.getValidChars(input));
    }

    @Test
    public void testGetValidCharsReplace() {
        String input = "A/B:C123::";
        assertEquals("Valid characters not as expected", "A~B~C123~~", QRCodeGenerator.getValidChars(input));
    }

    @Test
    public void testGetValidString() {
        String validString = QRCodeGenerator.getValidString();
        assertNotNull("Generated string is null", validString);
        assertTrue("Generated string does not start with 'fire_and_based_event_'", validString.startsWith("fire_and_based_event_"));
        assertEquals("Generated string length is not as expected", 28, validString.length());
    }

    @Test
    public void testGetValidCharsWithInvalidChars() {
        String input = "ABC#123";
        String expectedResult = "ABC~123"; // Invalid characters should be replaced with '~'
        assertEquals("Invalid characters not replaced", expectedResult, QRCodeGenerator.getValidChars(input));
    }

    @Test
    public void testGetValidStringWithEmptyValidChars() {
        String validString = QRCodeGenerator.getValidString();
        assertNotNull("Generated string is null", validString);
        assertTrue("Generated string does not start with 'fire_and_based_event_'", validString.startsWith("fire_and_based_event_"));
        assertEquals("Generated string length is not as expected", 28, validString.length());
    }

    @Test
    public void testGetValidStringWithNullValidChars() {
        QRCodeGenerator.validChars = null; // Setting validChars to null
        assertThrows(NullPointerException.class, QRCodeGenerator::getValidString);
    }
}

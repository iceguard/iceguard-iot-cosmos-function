package ch.iceguard.functions;

import com.fasterxml.jackson.core.JsonParser;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static ch.iceguard.functions.FieldReader.DEFAULT_DOUBLE;
import static ch.iceguard.functions.FieldReader.DEFAULT_INT;
import static ch.iceguard.functions.FieldReader.DEFAULT_STRING;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FieldReaderTest {

    private JsonParser jsonParser;
    private FieldReader fieldReader;

    @Before
    public void setUp() {
        jsonParser = mock(JsonParser.class);
        fieldReader = new FieldReader(jsonParser);
    }

    @Test
    public void readString() throws IOException {
        String expectedValue = "some";
        when(jsonParser.getValueAsString()).thenReturn(expectedValue);
        assertEquals("Parsed string", expectedValue, fieldReader.readString());
    }

    @Test
    public void readInt() throws IOException {
        int expectedValue = 10;
        when(jsonParser.getValueAsInt()).thenReturn(expectedValue);
        assertEquals("Parsed int", expectedValue, fieldReader.readInt());
    }

    @Test
    public void readDouble() throws IOException {
        double expectedValue = 10.99;
        when(jsonParser.getValueAsDouble()).thenReturn(expectedValue);
        assertEquals("Parsed int", expectedValue, fieldReader.readDouble(), 0);
    }

    @Test
    public void readStringOnException() throws IOException {
        when(jsonParser.getValueAsString()).thenThrow(IOException.class);
        assertEquals("Default string", DEFAULT_STRING, fieldReader.readString());
    }

    @Test
    public void readIntOnException() throws IOException {
        when(jsonParser.getValueAsInt()).thenThrow(IOException.class);
        assertEquals("Default int", DEFAULT_INT, fieldReader.readInt());
    }

    @Test
    public void readDoubleOnException() throws IOException {
        when(jsonParser.getValueAsDouble()).thenThrow(IOException.class);
        assertEquals("Default double", DEFAULT_DOUBLE, fieldReader.readDouble(), 0);
    }

}
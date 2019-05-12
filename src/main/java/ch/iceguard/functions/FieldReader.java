package ch.iceguard.functions;

import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;

/**
 * Decorates a {@link JsonParser} with exception handling.
 */
class FieldReader {

    static String DEFAULT_STRING = null;
    static int DEFAULT_INT = 0;
    static double DEFAULT_DOUBLE = 0;

    private final JsonParser jsonParser;

    FieldReader(JsonParser jsonParser) {
        this.jsonParser = jsonParser;
    }

    /**
     * Reads a string from the underlying {@link JsonParser}.
     *
     * @return parsed string or {@code DEFAULT_STRING} on exception
     */
    String readString() {
        String value;
        try {
            value = jsonParser.getValueAsString();
        } catch (IOException e) {
            System.out.println("Parser error: " + e.getMessage());
            value = DEFAULT_STRING;
        }
        return value;
    }

    /**
     * Reads an int from the underlying {@link JsonParser}.
     *
     * @return parsed int or {@code DEFAULT_INT} on exception
     */
    int readInt() {
        int value;
        try {
            value = jsonParser.getValueAsInt();
        } catch (IOException e) {
            System.out.println("Parser error: " + e.getMessage());
            value = DEFAULT_INT;
        }
        return value;
    }

    /**
     * Reads a double from the underlying {@link JsonParser}.
     *
     * @return parsed double or {@code DEFAULT_DOUBLE} on exception
     */
    double readDouble() {
        double value;
        try {
            value = jsonParser.getValueAsDouble();
        } catch (IOException e) {
            System.out.println("Parser error: " + e.getMessage());
            value = DEFAULT_DOUBLE;
        }
        return value;
    }
}

package ch.iceguard.functions;

import org.bson.Document;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static ch.iceguard.functions.DocumentParser.*;
import static org.junit.Assert.assertEquals;


public class DocumentParserTest {

    private static final Map<Field, String> MESSAGE_VALUES = new HashMap<>();

    static {
        MESSAGE_VALUES.put(Field.MESSAGE_ID, "348");
        MESSAGE_VALUES.put(Field.DEVICE_ID, "simulator-1");
        MESSAGE_VALUES.put(Field.TIMESTAMP, "0.0");
        MESSAGE_VALUES.put(Field.TEMPERATURE, "29.88");
        MESSAGE_VALUES.put(Field.HUMIDITY, "79.15");
        MESSAGE_VALUES.put(Field.STEPS, "34800");
        MESSAGE_VALUES.put(Field.ACCELERATOR_X, "11.41");
        MESSAGE_VALUES.put(Field.ACCELERATOR_Y, "21.53");
        MESSAGE_VALUES.put(Field.ACCELERATOR_Z, "17.03");
        MESSAGE_VALUES.put(Field.GYROSCOPE_X, "26.09");
        MESSAGE_VALUES.put(Field.GYROSCOPE_Y, "110.4");
        MESSAGE_VALUES.put(Field.GYROSCOPE_Z, "1006.0");
    }

    private DocumentParser documentParser;

    @Before
    public void setUp() {
        documentParser = new DocumentParser();
    }

    @Test
    public void applyCompleteMessage() {

        String deviceMessage = new Document(messageValues()).toJson();
        System.out.println("Message from device: " + deviceMessage);

        Document document = documentParser.apply(deviceMessage);
        System.out.println("Parsed: " + document);

        Field.metaDeviceTags().values().forEach(field ->
                assertDocumentValue(document, field));

        Document measurements = document.get(MEASUREMENT_VALUE_FIELD, Document.class);

        Field.measurementDeviceTags().values().forEach(field ->
                assertDocumentValue(measurements, field));
    }

    @Test
    public void applyDefaultMetaDataOnMessageWithMissingValues() {

        String deviceMessage = new Document(new HashMap<>()).toJson();
        System.out.println("Message from device: " + deviceMessage);

        Document document = documentParser.apply(deviceMessage);
        System.out.println("Parsed: " + document);

        assertDefaultValue(document, Field.MESSAGE_ID, DEFAULT_MESSAGE_ID);
        assertDefaultValue(document, Field.DEVICE_ID, DEFAULT_DEVICE_ID);
        assertDefaultValue(document, Field.TIMESTAMP, DEFAULT_TIMESTAMP);
    }

    private static void assertDefaultValue(Document document, Field field, Object def) {
        assertEquals("Default " + field.name(), String.valueOf(def), String.valueOf(document.get(field.getDbTag())));
    }

    private static void assertDocumentValue(Document document, Field field) {
        assertEquals(field.name(), MESSAGE_VALUES.get(field), String.valueOf(document.get(field.getDbTag())));
    }

    private static Map<String, Object> messageValues() {
        return MESSAGE_VALUES.entrySet().stream().collect(Collectors.toMap(o -> o.getKey().getDeviceTag(), Map.Entry::getValue));
    }
}
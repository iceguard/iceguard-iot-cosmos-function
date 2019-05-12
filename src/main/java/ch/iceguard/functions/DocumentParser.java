package ch.iceguard.functions;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.bson.Document;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Parses the incoming device message into a bson {@link Document}.
 */
public class DocumentParser implements Function<String, Document> {

    private static final Map<String, Field> META_DEVICE_FIELDS = Field.metaDeviceTags();
    private static final Map<String, Field> MEASUREMENT_DEVICE_FIELDS = Field.measurementDeviceTags();

    static final String MEASUREMENT_VALUE_FIELD = "values";
    static final int DEFAULT_MESSAGE_ID = 0;
    static final String DEFAULT_DEVICE_ID = "simulator";
    static final Timestamp DEFAULT_TIMESTAMP = Timestamp.valueOf(LocalDateTime.now());

    @Override
    public Document apply(String message) {
        try {
            return parse(message);
        } catch (IOException e) {
            throw new IllegalStateException("Issue parsing message", e);
        }
    }

    private Document parse(String message) throws IOException {

        JsonFactory factory = new JsonFactory();
        Document document;
        Document measurementValues;
        try (JsonParser parser = factory.createParser(message)) {
            document = new Document();
            measurementValues = new Document();

            while (!parser.isClosed()) {
                JsonToken jsonToken = parser.nextToken();

                if (JsonToken.FIELD_NAME.equals(jsonToken)) {
                    parser.nextToken();
                    setDocumentValue(parser, document, measurementValues);
                }
            }
        }
        document.append(MEASUREMENT_VALUE_FIELD, measurementValues);
        validateDocument(document);
        return document;
    }

    private void validateDocument(Document document) {
        ensureValueIsPresent(document, Field.MESSAGE_ID.getDbTag(), DEFAULT_MESSAGE_ID);
        ensureValueIsPresent(document, Field.DEVICE_ID.getDbTag(), DEFAULT_DEVICE_ID);
        ensureValueIsPresent(document, Field.TIMESTAMP.getDbTag(), DEFAULT_TIMESTAMP);
    }

    private static void ensureValueIsPresent(Document document, String dbTag, Object def) {
        if (!document.containsKey(dbTag) || document.get(dbTag) == null) {
            document.append(dbTag, def);
        }
    }


    private static void setDocumentValue(JsonParser parser, Document document, Document measurementValues) throws IOException {
        String fieldName = parser.getCurrentName();
        FieldReader fieldReader = new FieldReader(parser);

        Optional.ofNullable(META_DEVICE_FIELDS.get(fieldName))
                .ifPresent(field -> document.append(field.getDbTag(), field.apply(fieldReader)));

        Optional.ofNullable(MEASUREMENT_DEVICE_FIELDS.get(fieldName))
                .ifPresent(field -> measurementValues.append(field.getDbTag(), field.apply(fieldReader)));
    }
}

package ch.iceguard.functions;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.Cardinality;
import com.microsoft.azure.functions.annotation.EventHubTrigger;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.StorageAccount;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class Function {

    private static final String MESSAGE_ID = "msgId";
    private static final String DEVICE_ID = "devId";
    private static final String TIMESTAMP = "timestamp";
    private static final String MEASUREMENT_VALUE_FIELD = "values";

    @FunctionName("iot-cosmos-processor")
    @StorageAccount("storageAccount")
    public void iotHubToCosmosProcessor(
            @EventHubTrigger(name = "message", eventHubName = "samples-workitems", connection = "igss-iothub_events_IOTHUB", consumerGroup = "$Default", cardinality = Cardinality.MANY) String message,
            final ExecutionContext context) throws IOException {

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

        System.out.println("Hallo");
        try (MongoClient mongoClient = new MongoClient(new MongoClientURI(System.getenv("iceguard-iot_DOCUMENTDB")))) {
            MongoCollection<Document> collection = getMongoCollection(mongoClient);
            collection.insertOne(document);
            context.getLogger().info(String.format("Message no %s from device %s", document.get(MESSAGE_ID), document.get(DEVICE_ID)));
        }
    }

    private void validateDocument(Document document) {
        if (!document.containsKey("messageId")) {
            document.append("messageId", 0);
        }
        if (!document.containsKey("deviceId")) {
            document.append("deviceId", "simulator");
        }
        if (!document.containsKey(TIMESTAMP)) {
            document.append(TIMESTAMP, Timestamp.valueOf(LocalDateTime.now()));
        }
    }

    private static MongoCollection<Document> getMongoCollection(MongoClient client) {
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoDatabase database = client.getDatabase("iceguard-iot");
        return database.getCollection(System.getenv("collection-name-COSMOS")).withCodecRegistry(pojoCodecRegistry);
    }

    private static void setDocumentValue(JsonParser parser, Document document, Document measurementValues) throws IOException {
        String fieldName = parser.getCurrentName();
        if (MESSAGE_ID.equals(fieldName)) {
            document.append("messageId", parser.getValueAsInt());
        }
        if (DEVICE_ID.equals(fieldName)) {
            document.append("deviceId", parser.getValueAsString());
        }
        if ("temp".equals(fieldName)) {
            measurementValues.append("temperature", parser.getValueAsDouble());
        }
        if ("hum".equals(fieldName)) {
            measurementValues.append("humidity", parser.getValueAsDouble());
        }
        if (TIMESTAMP.equals(fieldName)) {
            document.append(TIMESTAMP, parser.getValueAsDouble());
        }
        if ("accX".equals(fieldName)) {
            measurementValues.append("acceleratorX", parser.getValueAsDouble());
        }
        if ("accY".equals(fieldName)) {
            measurementValues.append("acceleratorY", parser.getValueAsDouble());
        }
        if ("accZ".equals(fieldName)) {
            measurementValues.append("acceleratorZ", parser.getValueAsDouble());
        }
        if ("gyroX".equals(fieldName)) {
            measurementValues.append("gyroscopeX", parser.getValueAsDouble());
        }
        if ("gyroY".equals(fieldName)) {
            measurementValues.append("gyroscopeY", parser.getValueAsDouble());
        }
        if ("gyroZ".equals(fieldName)) {
            measurementValues.append("gyroscopeZ", parser.getValueAsDouble());
        }
        if ("step".equals(fieldName)) {
            measurementValues.append("steps", parser.getValueAsInt());
        }
    }
}

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

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Function {

    private static final String MESSAGE_ID = "messageId";
    private static final String DEVICE_ID = "deviceId";
    private static final String TIMESTAMP = "timestamp";

    @FunctionName("iot-cosmos-processor")
    @StorageAccount("storageAccount")
    public void iotHubToCosmosProcessor(
            @EventHubTrigger(name = "message", eventHubName = "samples-workitems", connection = "igss-iothub_events_IOTHUB", consumerGroup = "$Default", cardinality = Cardinality.MANY) String message,
            final ExecutionContext context) throws IOException {

        JsonFactory factory = new JsonFactory();
        JsonParser parser = factory.createParser(message);
        Document document = new Document();

        while (!parser.isClosed()) {
            JsonToken jsonToken = parser.nextToken();

            if (JsonToken.FIELD_NAME.equals(jsonToken)) {
                parser.nextToken();
                setDocumentValue(parser, document);
            }
        }
        validateDocument(document);

        try (MongoClient mongoClient = new MongoClient(new MongoClientURI(System.getenv("iceguard-iot_DOCUMENTDB")))) {
            MongoCollection<Document> collection = getMongoCollection(mongoClient);
            collection.insertOne(document);
            context.getLogger().info(String.format("Message no %s from device %s", document.get(MESSAGE_ID), document.get(DEVICE_ID)));
        }
    }

    private void validateDocument(Document document) {
        if (!document.containsKey(MESSAGE_ID)) {
            document.append(MESSAGE_ID, 0);
        }
        if (!document.containsKey(DEVICE_ID)) {
            document.append(DEVICE_ID, "simulator");
        }
        if (!document.containsKey(TIMESTAMP)) {
            document.append(TIMESTAMP, Timestamp.valueOf(LocalDateTime.now()));
        }
    }

    private static MongoCollection<Document> getMongoCollection(MongoClient client) {
        MongoDatabase database = client.getDatabase("iceguard-iot");
        return database.getCollection(System.getenv("collection-name-COSMOS"));
    }

    private static void setDocumentValue(JsonParser parser, Document document) throws IOException {
        String fieldName = parser.getCurrentName();
        if (MESSAGE_ID.equals(fieldName)) {
            document.append(MESSAGE_ID, parser.getValueAsInt());
        }
        if (DEVICE_ID.equals(fieldName)) {
            document.append(DEVICE_ID, parser.getValueAsString());
        }
        if ("temperature".equals(fieldName)) {
            document.append("temperature", parser.getValueAsDouble());
        }
        if ("humidity".equals(fieldName)) {
            document.append("humidity", parser.getValueAsDouble());
        }
        if (TIMESTAMP.equals(fieldName)) {
            document.append(TIMESTAMP, parser.getValueAsDouble());
        }
        if ("acceleratorX".equals(fieldName)) {
            document.append("acceleratorX", parser.getValueAsDouble());
        }
        if ("acceleratorY".equals(fieldName)) {
            document.append("acceleratorY", parser.getValueAsDouble());
        }
        if ("acceleratorZ".equals(fieldName)) {
            document.append("acceleratorZ", parser.getValueAsDouble());
        }
        if ("gyroscopeX".equals(fieldName)) {
            document.append("gyroscopeX", parser.getValueAsDouble());
        }
        if ("gyroscopeY".equals(fieldName)) {
            document.append("gyroscopeY", parser.getValueAsDouble());
        }
        if ("gyroscopeZ".equals(fieldName)) {
            document.append("gyroscopeZ", parser.getValueAsDouble());
        }
    }
}

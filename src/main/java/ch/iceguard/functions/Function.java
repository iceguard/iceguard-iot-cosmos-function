package ch.iceguard.functions;

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

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * IoT cosmos function
 */
public class Function {

    private static final String COLLECTION_NAME_ENV_KEY = "collection-name-COSMOS";
    private static final String MONGO_CLIENT_URI_ENV_KEY = "iceguard-iot_DOCUMENTDB";
    private static final String DATABASE_NAME = "iceguard-iot";

    private static final String MESSAGE_ID = Field.MESSAGE_ID.getDeviceTag();
    private static final String DEVICE_ID = Field.DEVICE_ID.getDeviceTag();

    private final DocumentParser documentParser = new DocumentParser();

    @FunctionName("iot-cosmos-processor")
    @StorageAccount("storageAccount")
    public void iotHubToCosmosProcessor(
            @EventHubTrigger(name = "message", eventHubName = "samples-workitems", connection = "igss-iothub_events_IOTHUB", consumerGroup = "$Default", cardinality = Cardinality.MANY) String message, final ExecutionContext context) {

        Document document = documentParser.apply(message);

        System.out.println("Hallo");
        try (MongoClient mongoClient = new MongoClient(new MongoClientURI(System.getenv(MONGO_CLIENT_URI_ENV_KEY)))) {
            MongoCollection<Document> collection = getMongoCollection(mongoClient);
            collection.insertOne(document);
            context.getLogger().info(String.format("Message no %s from device %s", document.get(MESSAGE_ID), document.get(DEVICE_ID)));
        }
    }

    private static MongoCollection<Document> getMongoCollection(MongoClient client) {
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoDatabase database = client.getDatabase(DATABASE_NAME);
        return database.getCollection(System.getenv(COLLECTION_NAME_ENV_KEY)).withCodecRegistry(pojoCodecRegistry);
    }
}

package ch.iceguard.functions;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {

    private Measurement measurement = new Measurement();

    @FunctionName("iot-cosmos-processor")
    @StorageAccount("storageAccount")
    @CosmosDBOutput(name = "igss",
            databaseName = "iceguard-iot",
            collectionName = "mx-measurements",
            createIfNotExists = true,
            connectionStringSetting = "igss_COSMOS_DB")
    public String iotHubToCosmosProcessor(
            @EventHubTrigger(name = "message", eventHubName = "samples-workitems", connection = "igss-iothub_events_IOTHUB", consumerGroup = "$Default", cardinality = Cardinality.MANY) String message,
            final ExecutionContext context) throws IOException {
        JsonFactory factory = new JsonFactory();
        context.getLogger().info(message);
        JsonParser parser = factory.createParser(message);

        while (!parser.isClosed()) {
            JsonToken jsonToken = parser.nextToken();

            if (JsonToken.FIELD_NAME.equals(jsonToken)) {
                String fieldName = parser.getCurrentName();

                parser.nextToken();

                if ("temperature".equals(fieldName)) {
                    measurement.setTemperature(parser.getValueAsDouble());
                }
                if ("humidity".equals(fieldName)) {
                    measurement.setHumidity(parser.getValueAsDouble());
                }
            }

        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(measurement);
    }
}

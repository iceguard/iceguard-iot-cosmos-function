package ch.iceguard.functions;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ch.iceguard.functions.Field.Category.MEASUREMENT;
import static ch.iceguard.functions.Field.Category.META;

/**
 * Field mappings of tags sent by the IoT device versus tags used by the cosmos DB for storing the values. Categorized into meta data and measurements.
 */
public enum Field implements Function<FieldReader, Object> {

    MESSAGE_ID(META, "msgId", "messageId", FieldReader::readInt),
    DEVICE_ID(META, "devId", "deviceId", FieldReader::readString),
    TEMPERATURE(MEASUREMENT, "temp", "temperature", FieldReader::readDouble),
    HUMIDITY(MEASUREMENT, "hum", "humidity", FieldReader::readDouble),
    TIMESTAMP(META, "timestamp", "timestamp", FieldReader::readDouble),
    ACCELERATOR_X(MEASUREMENT, "accX", "acceleratorX", FieldReader::readDouble),
    ACCELERATOR_Y(MEASUREMENT, "accY", "acceleratorY", FieldReader::readDouble),
    ACCELERATOR_Z(MEASUREMENT, "accZ", "acceleratorZ", FieldReader::readDouble),
    GYROSCOPE_X(MEASUREMENT, "gyroX", "gyroscopeX", FieldReader::readDouble),
    GYROSCOPE_Y(MEASUREMENT, "gyroY", "gyroscopeY", FieldReader::readDouble),
    GYROSCOPE_Z(MEASUREMENT, "gyroZ", "gyroscopeZ", FieldReader::readDouble),
    STEPS(MEASUREMENT, "step", "steps", FieldReader::readInt);

    private final Category category;
    private final String deviceTag;
    private final String dbTag;
    private final Function<FieldReader, Object> valueReader;

    Field(Category category, String deviceTag, String dbTag,
          Function<FieldReader, Object> valueReader) {
        this.category = category;
        this.deviceTag = deviceTag;
        this.dbTag = dbTag;
        this.valueReader = valueReader;
    }

    /**
     * Reads the field value from an input reader.
     *
     * @param fieldReader reader providing the values
     * @return field value
     */
    @Override
    public Object apply(FieldReader fieldReader) {
        return valueReader.apply(fieldReader);
    }

    /**
     * @return the device tag representation of this field
     */
    public String getDeviceTag() {
        return deviceTag;
    }

    /**
     * @return the db tag representation of this field
     */
    public String getDbTag() {
        return dbTag;
    }

    /**
     * @return all meta data fields
     */
    public static Map<String, Field> metaDeviceTags() {
        return deviceTags(META);
    }

    /**
     * @return all measurement fields
     */
    public static Map<String, Field> measurementDeviceTags() {
        return deviceTags(MEASUREMENT);
    }

    /**
     * @param filterCategory {@link Category} used to filter the fields
     * @return filtered map of device tags -> fields
     */
    private static Map<String, Field> deviceTags(Category filterCategory) {
        return Stream.of(Field.values()).filter(field -> field.category == filterCategory)
                .collect(Collectors.toMap(Field::getDeviceTag, field -> field));
    }

    /**
     * Field categories for grouping
     */
    enum Category {
        META, MEASUREMENT
    }
}

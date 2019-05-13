package ch.iceguard.functions;

import org.junit.Test;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FieldTest {

    @Test
    public void applyFieldReader() {
        int expectedValue = 42;
        FieldReader fieldReader = mock(FieldReader.class);
        when(fieldReader.readInt()).thenReturn(expectedValue);
        assertEquals("Function return val", expectedValue, Field.MESSAGE_ID.apply(fieldReader));
    }

    @Test
    public void eachFieldHasDBTag() {
        Stream.of(Field.values()).forEach(field ->
                assertNotNull("DB tag is set", field.getDbTag()));
    }

    @Test
    public void eachFieldHasDeviceTag() {
        Stream.of(Field.values()).forEach(field ->
                assertNotNull("Device tag is set", field.getDeviceTag()));
    }

    @Test
    public void mapsAllMetaDeviceTags() {
        Map<String, Field> fields = Field.metaDeviceTags();
        assertTrue(fields.containsKey(Field.MESSAGE_ID.getDeviceTag()));
        assertTrue(fields.containsKey(Field.DEVICE_ID.getDeviceTag()));
        assertTrue(fields.containsKey(Field.TIMESTAMP.getDeviceTag()));
        assertEquals(fields.size(), 3);
    }

    @Test
    public void mapsAllMeasurementDeviceTags() {
        Map<String, Field> fields = Field.measurementDeviceTags();
        assertTrue(fields.containsKey(Field.TEMPERATURE.getDeviceTag()));
        assertTrue(fields.containsKey(Field.HUMIDITY.getDeviceTag()));
        assertTrue(fields.containsKey(Field.ACCELERATOR_X.getDeviceTag()));
        assertTrue(fields.containsKey(Field.ACCELERATOR_Y.getDeviceTag()));
        assertTrue(fields.containsKey(Field.ACCELERATOR_Z.getDeviceTag()));
        assertTrue(fields.containsKey(Field.GYROSCOPE_X.getDeviceTag()));
        assertTrue(fields.containsKey(Field.GYROSCOPE_Y.getDeviceTag()));
        assertTrue(fields.containsKey(Field.GYROSCOPE_Z.getDeviceTag()));
        assertTrue(fields.containsKey(Field.STEPS.getDeviceTag()));
        assertEquals(fields.size(), 9);
    }
}
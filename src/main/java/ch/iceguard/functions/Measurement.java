package ch.iceguard.functions;

public class Measurement {

    private double temperature;
    private double humidity;

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    @Override
    public String toString() {
        return "Measurement{" +
                "temperature=" + temperature +
                ", humidity=" + humidity +
                '}';
    }
}

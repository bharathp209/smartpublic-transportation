public class Stop {

    private String stopId;
    private String stopName;

    // Constructor
    public Stop(String stopId, String stopName) {
        this.stopId = stopId;
        this.stopName = stopName;
    }

    // Getter for stop ID
    public String getStopId() {
        return stopId;
    }

    // Getter for stop name
    public String getStopName() {
        return stopName;
    }
}
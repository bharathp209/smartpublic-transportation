public class Road {

    private String source;
    private String destination;
    private int travelTime;

    // Constructor
    public Road(String source, String destination, int travelTime) {
        this.source = source;
        this.destination = destination;
        this.travelTime = travelTime;
    }

    // Get the starting stop ID
    public String getSource() {
        return source;
    }

    // Get the destination stop ID
    public String getDestination() {
        return destination;
    }

    // Get the travel time
    public int getTravelTime() {
        return travelTime;
    }

    // Update travel time when traffic conditions change
    public void setTravelTime(int travelTime) {
        if (travelTime > 0) {
            this.travelTime = travelTime;
        } else {
            System.out.println("Travel time must be greater than zero.");
        }
    }
}
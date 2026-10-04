public class Road {

    private Stop destination;
    private int travelTime;

    // Constructor
    public Road(Stop destination, int travelTime) {
        this.destination = destination;
        this.travelTime = travelTime;
    }

    // Getter for destination
    public Stop getDestination() {
        return destination;
    }

    // Getter for travel time
    public int getTravelTime() {
        return travelTime;
    }
}
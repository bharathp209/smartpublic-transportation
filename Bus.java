public class Bus {

    private String busId;
    private int capacity;
    private int currentPassengers;
    private String status;

    // Constructor
    public Bus(String busId, int capacity) {
        this.busId = busId;
        this.capacity = capacity;
        this.currentPassengers = 0;
        this.status = "AVAILABLE";
    }

    // Board passengers
    public int boardPassengers(int passengers) {

        int availableSeats = capacity - currentPassengers;

        int boardedPassengers = Math.min(passengers, availableSeats);

        currentPassengers = currentPassengers + boardedPassengers;

        status = "BUSY";

        return boardedPassengers;
    }

    // Complete trip
    public void completeTrip() {
        currentPassengers = 0;
        status = "AVAILABLE";
    }

    // Getters
    public String getBusId() {
        return busId;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getCurrentPassengers() {
        return currentPassengers;
    }

    public String getStatus() {
        return status;
    }
}
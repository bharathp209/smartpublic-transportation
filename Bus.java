public class Bus {

    private String busId;
    private int capacity;
    private int currentPassengers;
    private String status;

    public Bus(String busId, int capacity) {
        this.busId = busId;
        this.capacity = capacity;
        this.currentPassengers = 0;
        this.status = "AVAILABLE";
    }

    // Get the bus ID
    public String getBusId() {
        return busId;
    }

    // Get the total passenger capacity
    public int getCapacity() {
        return capacity;
    }

    // Get the current passenger count
    public int getCurrentPassengers() {
        return currentPassengers;
    }

    // Get the current bus status
    public String getStatus() {
        return status;
    }

    // Calculate the remaining seats
    public int getAvailableSeats() {
        return capacity - currentPassengers;
    }

    // Check whether the bus can accept a new trip
    public boolean isAvailable() {
        return status.equals("AVAILABLE");
    }

    // Board passengers without exceeding capacity
    public int boardPassengers(int passengers) {

        if (passengers <= 0) {
            return 0;
        }

        if (!status.equals("AVAILABLE")) {
            return 0;
        }

        int boardedPassengers = Math.min(
            passengers,
            getAvailableSeats()
        );

        currentPassengers += boardedPassengers;

        if (currentPassengers > 0) {
            status = "BUSY";
        }

        return boardedPassengers;
    }

    // Complete a successfully finished trip
    public void completeTrip() {

        if (status.equals("BROKEN_DOWN")) {
            System.out.println(
                "Cannot release bus " + busId
                + ": it is marked as broken down."
            );
            return;
        }

        currentPassengers = 0;
        status = "AVAILABLE";
    }

    // Mark the bus as broken down
    public void breakdown() {

        if (status.equals("BROKEN_DOWN")) {
            System.out.println(
                "Bus " + busId + " is already broken down."
            );
            return;
        }

        status = "BROKEN_DOWN";
    }

    // Repair the bus
    public void repair() {

        if (!status.equals("BROKEN_DOWN")) {
            System.out.println(
                "Bus " + busId + " is not broken down."
            );
            return;
        }

        if (currentPassengers > 0) {
            status = "BUSY";
        } else {
            status = "AVAILABLE";
        }
    }

    // Check whether this bus can use a road
    public boolean canUseRoad(int travelTime) {
        return travelTime > 0;
    }
}
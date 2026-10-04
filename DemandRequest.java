public class DemandRequest {

    private String requestId;
    private Stop pickupStop;
    private Stop destinationStop;
    private int passengerCount;
    private int arrivalOrder;

    // Constructor
    public DemandRequest(String requestId, Stop pickupStop,
                         Stop destinationStop, int passengerCount,
                         int arrivalOrder) {

        this.requestId = requestId;
        this.pickupStop = pickupStop;
        this.destinationStop = destinationStop;
        this.passengerCount = passengerCount;
        this.arrivalOrder = arrivalOrder;
    }

    // Getters
    public String getRequestId() {
        return requestId;
    }

    public Stop getPickupStop() {
        return pickupStop;
    }

    public Stop getDestinationStop() {
        return destinationStop;
    }

    public int getPassengerCount() {
        return passengerCount;
    }

    public int getArrivalOrder() {
        return arrivalOrder;
    }
}
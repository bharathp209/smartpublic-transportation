import java.util.List;

public class ReliefTrip {

    private String tripId;
    private DemandRequest request;
    private Bus bus;
    private List<String> route;
    private int travelTime;
    private String status;

    public ReliefTrip(String tripId, DemandRequest request,
                      Bus bus, List<String> route,
                      int travelTime) {

        this.tripId = tripId;
        this.request = request;
        this.bus = bus;
        this.route = route;
        this.travelTime = travelTime;
        this.status = "ASSIGNED";
    }

    public String getTripId() {
        return tripId;
    }

    public DemandRequest getRequest() {
        return request;
    }

    public Bus getBus() {
        return bus;
    }

    public List<String> getRoute() {
        return route;
    }

    public int getTravelTime() {
        return travelTime;
    }

    public String getStatus() {
        return status;
    }

    public void startTrip() {
        status = "IN_PROGRESS";
    }

    public void completeTrip() {
        status = "COMPLETED";
        bus.completeTrip();
    }
}
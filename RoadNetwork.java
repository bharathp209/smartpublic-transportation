import java.util.*;

public class RoadNetwork {

    private Map<String, Stop> stops;
    private Map<String, List<Road>> roads;

    // Constructor
    public RoadNetwork() {
        stops = new HashMap<>();
        roads = new HashMap<>();
    }

    // Add a stop
    public void addStop(Stop stop) {
        stops.put(stop.getStopId(), stop);
        roads.put(stop.getStopId(), new ArrayList<>());
    }

    // Add a road
    public void addRoad(String fromStopId, String toStopId, int travelTime) {

        Stop destination = stops.get(toStopId);

        Road road = new Road(destination, travelTime);

        roads.get(fromStopId).add(road);
    }

    // Get a stop
    public Stop getStop(String stopId) {
        return stops.get(stopId);
    }

    // Get roads connected from a stop
    public List<Road> getRoads(String stopId) {
        return roads.get(stopId);
    }
}
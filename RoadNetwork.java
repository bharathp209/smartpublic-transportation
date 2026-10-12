import java.util.*;

public class RoadNetwork {

    // Stores all stops using their stop IDs
    private Map<String, Stop> stops;

    // Stores the roads connected to each stop
    private Map<String, List<Road>> adjacencyList;

    // Constructor
    public RoadNetwork() {
        stops = new LinkedHashMap<>();
        adjacencyList = new LinkedHashMap<>();
    }

    // Add a stop to the network
    public void addStop(Stop stop) {

        if (stop == null) {
            System.out.println("Invalid stop.");
            return;
        }

        String stopId = stop.getStopId();

        if (stops.containsKey(stopId)) {
            System.out.println(
                "Stop ID already exists: " + stopId
            );
            return;
        }

        stops.put(stopId, stop);
        adjacencyList.put(stopId, new ArrayList<>());

        System.out.println(
            "Stop added successfully: " + stop.getStopName()
        );
    }

    // Find a stop using its ID
    public Stop getStop(String stopId) {
        return stops.get(stopId);
    }

    // Get all stop IDs
    public Set<String> getStopIds() {
        return new LinkedHashSet<>(stops.keySet());
    }

    // Get all stops
    public Collection<Stop> getAllStops() {
        return new ArrayList<>(stops.values());
    }

    // Check whether a stop exists
    public boolean containsStop(String stopId) {
        return stops.containsKey(stopId);
    }

    // Add a directed road between two stops
    public void addRoad(
        String fromStopId,
        String toStopId,
        int travelTime
    ) {

        if (!stops.containsKey(fromStopId)
                || !stops.containsKey(toStopId)) {

            System.out.println(
                "Cannot add road: one or both stops do not exist."
            );
            return;
        }

        if (fromStopId.equals(toStopId)) {
            System.out.println(
                "A road cannot connect a stop to itself."
            );
            return;
        }

        if (travelTime <= 0) {
            System.out.println(
                "Travel time must be greater than zero."
            );
            return;
        }

        List<Road> roads = adjacencyList.get(fromStopId);

        // Update the road if the connection already exists
        for (Road road : roads) {

            if (road.getDestination().equals(toStopId)) {
                road.setTravelTime(travelTime);

                System.out.println(
                    "Road travel time updated: "
                    + fromStopId + " -> " + toStopId
                    + " (" + travelTime + " minutes)"
                );
                return;
            }
        }

        // Otherwise, create a new road
        Road road = new Road(
            fromStopId,
            toStopId,
            travelTime
        );

        roads.add(road);

        System.out.println(
            "Road added: "
            + fromStopId + " -> " + toStopId
            + " (" + travelTime + " minutes)"
        );
    }

    // Get roads leaving a particular stop
    public List<Road> getRoads(String stopId) {

        List<Road> roads = adjacencyList.get(stopId);

        if (roads == null) {
            return Collections.emptyList();
        }

        return Collections.unmodifiableList(roads);
    }

    // Display every stop and its outgoing roads
    public void displayNetwork() {

        System.out.println("\n===== ROAD NETWORK =====");

        if (stops.isEmpty()) {
            System.out.println("No stops have been added.");
            return;
        }

        for (String stopId : stops.keySet()) {

            Stop stop = stops.get(stopId);

            System.out.println(
                "\nStop: " + stop.getStopName()
                + " (" + stopId + ")"
            );

            List<Road> roads = adjacencyList.get(stopId);

            if (roads.isEmpty()) {
                System.out.println("  No outgoing roads.");
            } else {

                for (Road road : roads) {
                    System.out.println(
                        "  -> " + road.getDestination()
                        + " | Travel time: "
                        + road.getTravelTime() + " minutes"
                    );
                }
            }
        }
    }
}
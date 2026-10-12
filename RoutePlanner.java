import java.util.*;

public class RoutePlanner {

    private RoadNetwork roadNetwork;

    // Constructor
    public RoutePlanner(RoadNetwork roadNetwork) {
        this.roadNetwork = roadNetwork;
    }

    // Find the shortest-time route between two stops
    public List<String> findShortestRoute(
            String startStopId,
            String destinationStopId) {

        List<String> route = new ArrayList<>();

        // Check whether both stops exist
        if (!roadNetwork.containsStop(startStopId)
                || !roadNetwork.containsStop(destinationStopId)) {
            return route;
        }

        // If the start and destination are the same
        if (startStopId.equals(destinationStopId)) {
            route.add(startStopId);
            return route;
        }

        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previousStops = new HashMap<>();

        PriorityQueue<StopDistance> queue =
                new PriorityQueue<>();

        // Initialize all distances to infinity
        for (String stopId : roadNetwork.getStopIds()) {
            distances.put(stopId, Integer.MAX_VALUE);
        }

        distances.put(startStopId, 0);
        queue.offer(new StopDistance(startStopId, 0));

        // Dijkstra's algorithm
        while (!queue.isEmpty()) {

            StopDistance current = queue.poll();

            String currentStop = current.getStopId();
            int currentDistance = current.getDistance();

            // Ignore outdated queue entries
            if (currentDistance > distances.get(currentStop)) {
                continue;
            }

            // Destination reached
            if (currentStop.equals(destinationStopId)) {
                break;
            }

            // Examine outgoing roads
            for (Road road : roadNetwork.getRoads(currentStop)) {

                String nextStop = road.getDestination();
                int travelTime = road.getTravelTime();

                int newDistance = currentDistance + travelTime;

                if (newDistance < distances.get(nextStop)) {

                    distances.put(nextStop, newDistance);
                    previousStops.put(nextStop, currentStop);

                    queue.offer(
                            new StopDistance(nextStop, newDistance)
                    );
                }
            }
        }

        // Check whether a route exists
        if (distances.get(destinationStopId) == Integer.MAX_VALUE) {
            return route;
        }

        // Reconstruct the route from destination to source
        String currentStop = destinationStopId;

        while (currentStop != null) {

            route.add(currentStop);

            if (currentStop.equals(startStopId)) {
                break;
            }

            currentStop = previousStops.get(currentStop);
        }

        Collections.reverse(route);

        return route;
    }

    // Calculate shortest travel time between two stops
    public int getTravelTime(
            String startStopId,
            String destinationStopId) {

        if (!roadNetwork.containsStop(startStopId)
                || !roadNetwork.containsStop(destinationStopId)) {
            return -1;
        }

        if (startStopId.equals(destinationStopId)) {
            return 0;
        }

        Map<String, Integer> distances = new HashMap<>();

        PriorityQueue<StopDistance> queue =
                new PriorityQueue<>();

        // Initialize distances
        for (String stopId : roadNetwork.getStopIds()) {
            distances.put(stopId, Integer.MAX_VALUE);
        }

        distances.put(startStopId, 0);
        queue.offer(new StopDistance(startStopId, 0));

        while (!queue.isEmpty()) {

            StopDistance current = queue.poll();

            String currentStop = current.getStopId();
            int currentDistance = current.getDistance();

            // Skip outdated entries
            if (currentDistance > distances.get(currentStop)) {
                continue;
            }

            // Return the shortest time when destination is reached
            if (currentStop.equals(destinationStopId)) {
                return currentDistance;
            }

            for (Road road : roadNetwork.getRoads(currentStop)) {

                String nextStop = road.getDestination();

                int newDistance =
                        currentDistance + road.getTravelTime();

                if (newDistance < distances.get(nextStop)) {

                    distances.put(nextStop, newDistance);

                    queue.offer(
                            new StopDistance(nextStop, newDistance)
                    );
                }
            }
        }

        return -1;
    }

    // Calculate the total travel time of a supplied route
    public int getTravelTime(List<String> route) {

        if (route == null || route.isEmpty()) {
            return -1;
        }

        if (route.size() == 1) {
            return 0;
        }

        int totalTime = 0;

        for (int i = 0; i < route.size() - 1; i++) {

            String source = route.get(i);
            String destination = route.get(i + 1);

            int roadTime = getDirectRoadTravelTime(
                    source,
                    destination
            );

            if (roadTime < 0) {
                return -1;
            }

            totalTime += roadTime;
        }

        return totalTime;
    }

    // Get travel time for one direct road
    private int getDirectRoadTravelTime(
            String source,
            String destination) {

        for (Road road : roadNetwork.getRoads(source)) {

            if (road.getDestination().equals(destination)) {
                return road.getTravelTime();
            }
        }

        return -1;
    }

    // Helper class for the priority queue
    private static class StopDistance
            implements Comparable<StopDistance> {

        private String stopId;
        private int distance;

        public StopDistance(String stopId, int distance) {
            this.stopId = stopId;
            this.distance = distance;
        }

        public String getStopId() {
            return stopId;
        }

        public int getDistance() {
            return distance;
        }

        @Override
        public int compareTo(StopDistance other) {
            return Integer.compare(
                    this.distance,
                    other.distance
            );
        }
    }
}
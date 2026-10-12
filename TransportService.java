import java.util.*;

public class TransportService {

    private Map<String, Bus> buses;

    private PriorityQueue<DemandRequest> demandQueue;

    private List<ReliefTrip> trips;

    private DemandPolicy demandPolicy;

    private RoadNetwork roadNetwork;

    private RoutePlanner routePlanner;

    private int tripCounter = 1;

    private int requestCounter = 1;


    // Constructor used by Main.java
    public TransportService(
            RoadNetwork network,
            RoutePlanner planner) {

        this.roadNetwork = network;
        this.routePlanner = planner;

        buses = new LinkedHashMap<>();
        trips = new ArrayList<>();

        demandPolicy = new DemandPolicy();

        demandQueue = new PriorityQueue<>(
                demandPolicy.getPriorityComparator()
        );
    }


    // Compatibility constructor for existing code
    public TransportService(RoadNetwork network) {

        this(
                network,
                new RoutePlanner(network)
        );
    }


    // Add a bus
    public void addBus(Bus bus) {

        if (bus == null) {
            System.out.println("Invalid bus.");
            return;
        }

        String busId = bus.getBusId();

        if (buses.containsKey(busId)) {
            System.out.println(
                    "Bus ID already exists: " + busId
            );
            return;
        }

        buses.put(busId, bus);

        System.out.println(
                "Bus " + busId + " added successfully."
        );
    }


    // Submit passenger demand
    public void submitDemand(DemandRequest request) {

        addDemand(request);
    }


    // Add demand to the priority queue
    public void addDemand(DemandRequest request) {

        if (request == null) {
            System.out.println(
                    "Invalid demand request."
            );
            return;
        }

        if (request.getPassengerCount() <= 0) {
            System.out.println(
                    "Passenger count must be greater than zero."
            );
            return;
        }

        String pickupId =
                request.getPickupStop().getStopId();

        String destinationId =
                request.getDestinationStop().getStopId();

        if (!roadNetwork.containsStop(pickupId)
                || !roadNetwork.containsStop(destinationId)) {

            System.out.println(
                    "Pickup or destination stop does not exist."
            );
            return;
        }

        if (pickupId.equals(destinationId)) {
            System.out.println(
                    "Pickup and destination must be different."
            );
            return;
        }

        demandQueue.offer(request);

        System.out.println(
                "Demand request "
                        + request.getRequestId()
                        + " added successfully."
        );
    }


    // Find the best available bus
    private Bus findSuitableBus(
            DemandRequest request) {

        Bus bestBus = null;

        int bestTravelTime = Integer.MAX_VALUE;

        int bestCapacity = Integer.MAX_VALUE;

        String pickupId =
                request.getPickupStop().getStopId();

        String destinationId =
                request.getDestinationStop().getStopId();


        // Calculate depot-to-pickup route
        List<String> pickupRoute =
                routePlanner.findShortestRoute(
                        "DEPOT",
                        pickupId
                );

        if (pickupRoute.isEmpty()) {
            return null;
        }

        int pickupTime =
                routePlanner.getTravelTime(pickupRoute);

        if (pickupTime < 0) {
            return null;
        }


        // Calculate passenger route
        List<String> passengerRoute =
                routePlanner.findShortestRoute(
                        pickupId,
                        destinationId
                );

        if (passengerRoute.isEmpty()) {
            return null;
        }

        int passengerTime =
                routePlanner.getTravelTime(passengerRoute);

        if (passengerTime < 0) {
            return null;
        }

        int totalTravelTime =
                pickupTime + passengerTime;


        // Find a suitable available bus
        for (Bus bus : buses.values()) {

            if (!bus.isAvailable()) {
                continue;
            }

            if (bus.getAvailableSeats() <= 0) {
                continue;
            }

            /*
             * A smaller bus may carry part of the demand.
             * Remaining passengers will stay in the queue.
             */

            if (bestBus == null
                    || totalTravelTime < bestTravelTime
                    || (totalTravelTime == bestTravelTime
                    && bus.getCapacity() < bestCapacity)) {

                bestBus = bus;

                bestTravelTime = totalTravelTime;

                bestCapacity = bus.getCapacity();
            }
        }

        return bestBus;
    }


    // Allocate available buses to passenger demands
    public void allocateBuses() {

        if (demandQueue.isEmpty()) {

            System.out.println(
                    "No pending passenger demands."
            );

            return;
        }

        System.out.println(
                "\n========== BUS ALLOCATION =========="
        );

        int requestsToProcess = demandQueue.size();

        int allocatedCount = 0;


        /*
         * Process only the requests present at the beginning
         * of this allocation cycle.
         *
         * Newly created remaining-demand requests wait
         * until the next allocation cycle.
         */

        for (int i = 0; i < requestsToProcess; i++) {

            DemandRequest request = demandQueue.poll();

            if (request == null) {
                break;
            }


            // Find a suitable bus
            Bus selectedBus =
                    findSuitableBus(request);

            if (selectedBus == null) {

                demandQueue.offer(request);

                System.out.println(
                        "Request " + request.getRequestId()
                        + " remains pending because no suitable "
                        + "bus or route is available."
                );

                continue;
            }


            String pickupId =
                    request.getPickupStop().getStopId();

            String destinationId =
                    request.getDestinationStop().getStopId();


            // Recalculate routes using current road travel times
            List<String> pickupRoute =
                    routePlanner.findShortestRoute(
                            "DEPOT",
                            pickupId
                    );

            List<String> passengerRoute =
                    routePlanner.findShortestRoute(
                            pickupId,
                            destinationId
                    );


            if (pickupRoute.isEmpty()
                    || passengerRoute.isEmpty()) {

                demandQueue.offer(request);

                continue;
            }


            int pickupTime =
                    routePlanner.getTravelTime(pickupRoute);

            int passengerTime =
                    routePlanner.getTravelTime(passengerRoute);


            if (pickupTime < 0 || passengerTime < 0) {

                demandQueue.offer(request);

                continue;
            }


            int totalTravelTime =
                    pickupTime + passengerTime;


            // Board as many passengers as the bus can carry
            int requestedPassengers =
                    request.getPassengerCount();

            int boardedPassengers =
                    selectedBus.boardPassengers(
                            requestedPassengers
                    );


            if (boardedPassengers <= 0) {

                demandQueue.offer(request);

                continue;
            }


            // Combine depot-to-pickup and passenger routes
            List<String> completeRoute =
                    new ArrayList<>();

            completeRoute.addAll(pickupRoute);

            for (int j = 1;
                    j < passengerRoute.size();
                    j++) {

                completeRoute.add(
                        passengerRoute.get(j)
                );
            }


            // Create the relief trip
            ReliefTrip trip = new ReliefTrip(
                    "T" + tripCounter++,
                    request,
                    selectedBus,
                    completeRoute,
                    totalTravelTime
            );

            // Mark the trip as started
            trip.startTrip();

            trips.add(trip);

            allocatedCount++;


            // Display allocation details
            System.out.println(
                    "\nRequest ID: "
                            + request.getRequestId()
            );

            System.out.println(
                    "Trip ID: " + trip.getTripId()
            );

            System.out.println(
                    "Bus assigned: "
                            + selectedBus.getBusId()
            );

            System.out.println(
                    "Passengers requested: "
                            + requestedPassengers
            );

            System.out.println(
                    "Passengers boarded: "
                            + boardedPassengers
            );

            System.out.println(
                    "Passengers remaining: "
                            + (requestedPassengers
                            - boardedPassengers)
            );

            System.out.println(
                    "Depot-to-pickup route: "
                            + pickupRoute
            );

            System.out.println(
                    "Pickup-to-destination route: "
                            + passengerRoute
            );

            System.out.println(
                    "Complete route: "
                            + completeRoute
            );

            System.out.println(
                    "Estimated travel time: "
                            + totalTravelTime
                            + " minutes"
            );

            System.out.println(
                    "Trip status: " + trip.getStatus()
            );


            // Preserve passengers who could not board
            int remainingPassengers =
                    requestedPassengers - boardedPassengers;

            if (remainingPassengers > 0) {

                DemandRequest remainingRequest =
                        new DemandRequest(
                                "R" + requestCounter++,
                                request.getPickupStop(),
                                request.getDestinationStop(),
                                remainingPassengers,
                                request.getArrivalOrder()
                        );

                demandQueue.offer(remainingRequest);

                System.out.println(
                        remainingPassengers
                        + " passengers remain in the demand queue."
                );
            }

            System.out.println(
                    "------------------------------------"
            );
        }


        System.out.println(
                "\nAllocation process completed."
        );

        System.out.println(
                "Trips allocated: " + allocatedCount
        );

        System.out.println(
                "Pending requests: " + demandQueue.size()
        );

        System.out.println(
                "===================================="
        );
    }


    // Display registered buses
    public void displayBuses() {

        System.out.println(
                "\n========== REGISTERED BUSES =========="
        );

        if (buses.isEmpty()) {

            System.out.println(
                    "No buses registered."
            );

            return;
        }

        for (Bus bus : buses.values()) {

            System.out.println(
                    "Bus ID: " + bus.getBusId()
                    + " | Capacity: " + bus.getCapacity()
                    + " | Passengers: "
                    + bus.getCurrentPassengers()
                    + " | Status: " + bus.getStatus()
            );
        }
    }


    // Display pending demand in priority order
    public void displayPendingDemand() {

        System.out.println(
                "\n========== PENDING DEMANDS =========="
        );

        if (demandQueue.isEmpty()) {

            System.out.println(
                    "No pending demands."
            );

            return;
        }

        // Use a copy to preserve the original queue
        PriorityQueue<DemandRequest> tempQueue =
                new PriorityQueue<>(demandQueue);

        while (!tempQueue.isEmpty()) {

            DemandRequest request = tempQueue.poll();

            System.out.println(
                    "Request ID: "
                    + request.getRequestId()
                    + " | Pickup: "
                    + request.getPickupStop().getStopName()
                    + " | Destination: "
                    + request.getDestinationStop().getStopName()
                    + " | Passengers: "
                    + request.getPassengerCount()
                    + " | Arrival order: "
                    + request.getArrivalOrder()
            );
        }
    }


    // Display all relief trips
    public void displayTrips() {

        System.out.println(
                "\n========== RELIEF TRIPS =========="
        );

        if (trips.isEmpty()) {

            System.out.println(
                    "No relief trips created."
            );

            return;
        }

        for (ReliefTrip trip : trips) {

            System.out.println(
                    "Trip ID: " + trip.getTripId()
                    + " | Bus: "
                    + trip.getBus().getBusId()
                    + " | Passengers: "
                    + trip.getRequest().getPassengerCount()
                    + " | Status: " + trip.getStatus()
                    + " | Route: " + trip.getRoute()
                    + " | Estimated time: "
                    + trip.getTravelTime() + " minutes"
            );
        }
    }


    // Complete a trip
    public void completeTrip(String tripId) {

        for (ReliefTrip trip : trips) {

            if (!trip.getTripId().equalsIgnoreCase(tripId)) {
                continue;
            }

            if ("COMPLETED".equals(trip.getStatus())) {

                System.out.println(
                        "This trip is already completed."
                );

                return;
            }

            if ("ASSIGNED".equals(trip.getStatus())) {

                System.out.println(
                        "This trip has not started yet."
                );

                return;
            }

            if ("BROKEN_DOWN".equals(
                    trip.getBus().getStatus())) {

                System.out.println(
                        "This bus is broken down. Repair it "
                        + "before completing its trip."
                );

                return;
            }

            trip.completeTrip();

            System.out.println(
                    "Trip " + trip.getTripId()
                    + " completed successfully."
            );

            System.out.println(
                    "Bus " + trip.getBus().getBusId()
                    + " is now available."
            );

            return;
        }

        System.out.println(
                "Trip ID not found."
        );
    }


    // Report a bus breakdown
    public void breakdownBus(String busId) {

        Bus bus = buses.get(busId);

        if (bus == null) {

            System.out.println(
                    "Bus ID not found."
            );

            return;
        }

        if ("BROKEN_DOWN".equals(bus.getStatus())) {

            System.out.println(
                    "This bus is already broken down."
            );

            return;
        }

        bus.breakdown();

        System.out.println(
                "Bus " + busId + " has broken down."
        );

        if (bus.getCurrentPassengers() > 0) {

            System.out.println(
                    "Warning: this bus has passengers aboard. "
                    + "Arrange a replacement bus."
            );
        }
    }


    // Repair a broken-down bus
    public void repairBus(String busId) {

        Bus bus = buses.get(busId);

        if (bus == null) {

            System.out.println(
                    "Bus ID not found."
            );

            return;
        }

        if (!"BROKEN_DOWN".equals(bus.getStatus())) {

            System.out.println(
                    "This bus is not broken down."
            );

            return;
        }

        bus.repair();

        System.out.println(
                "Bus " + busId + " has been repaired."
        );

        System.out.println(
                "Current status: " + bus.getStatus()
        );
    }


    // Access the route planner
    public RoutePlanner getRoutePlanner() {

        return routePlanner;
    }


    // Access the registered road network
    public RoadNetwork getRoadNetwork() {

        return roadNetwork;
    }
}
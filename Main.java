import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    private static final RoadNetwork roadNetwork = new RoadNetwork();

    private static final RoutePlanner routePlanner =
            new RoutePlanner(roadNetwork);

    private static final TransportService transportService =
            new TransportService(roadNetwork, routePlanner);

    private static int requestCounter = 1;
    private static int arrivalCounter = 1;

    public static void main(String[] args) {

        boolean running = true;

        System.out.println("========================================");
        System.out.println("       VETRITRANSIT SYSTEM");
        System.out.println(" Smart Public Transportation Route");
        System.out.println("          Optimization");
        System.out.println("========================================");

        while (running) {

            displayMenu();

            int choice = readInt("Enter your choice: ");

            switch (choice) {

                case 1:
                    addBus();
                    break;

                case 2:
                    addStop();
                    break;

                case 3:
                    addRoad();
                    break;

                case 4:
                    submitDemand();
                    break;

                case 5:
                    transportService.displayBuses();
                    break;

                case 6:
                    roadNetwork.displayNetwork();
                    break;

                case 7:
                    transportService.displayPendingDemand();
                    break;

                case 8:
                    findRoute();
                    break;

                case 9:
                    transportService.allocateBuses();
                    break;

                case 10:
                    transportService.displayTrips();
                    break;

                case 11:
                    completeTrip();
                    break;

                case 12:
                    breakdownBus();
                    break;

                case 13:
                    repairBus();
                    break;

                case 14:
                    updateTraffic();
                    break;

                case 15:
                    displayShortestRoute();
                    break;

                case 0:
                    running = false;
                    System.out.println(
                            "\nThank you for using VetriTransit!"
                    );
                    break;

                default:
                    System.out.println(
                            "Invalid choice. Please try again."
                    );
            }

            System.out.println();
        }

        scanner.close();
    }

    // Display the main menu
    private static void displayMenu() {

        System.out.println("========== MAIN MENU ==========");
        System.out.println("1.  Add Bus");
        System.out.println("2.  Add Stop");
        System.out.println("3.  Add Road");
        System.out.println("4.  Submit Passenger Demand");
        System.out.println("5.  Display Buses");
        System.out.println("6.  Display Road Network");
        System.out.println("7.  Display Pending Demand");
        System.out.println("8.  Find Shortest Route");
        System.out.println("9.  Allocate Buses");
        System.out.println("10. Display Trips");
        System.out.println("11. Complete Trip");
        System.out.println("12. Report Bus Breakdown");
        System.out.println("13. Repair Bus");
        System.out.println("14. Update Traffic Travel Time");
        System.out.println("15. Recalculate Shortest Route");
        System.out.println("0.  Exit");
        System.out.println("================================");
    }

    // Add a bus
    private static void addBus() {

        System.out.println("\n--- ADD BUS ---");

        System.out.println("1. Mini Bus");
        System.out.println("2. Standard Bus");

        int type = readInt("Select bus type: ");

        if (type != 1 && type != 2) {
            System.out.println("Invalid bus type.");
            return;
        }

        String busId = readText("Enter bus ID: ");

        int capacity = readInt("Enter passenger capacity: ");

        if (busId.isEmpty() || capacity <= 0) {
            System.out.println(
                    "Bus ID cannot be empty and capacity must be positive."
            );
            return;
        }

        if (type == 1) {
            transportService.addBus(
                    new MiniBus(busId, capacity)
            );
        } else {
            transportService.addBus(
                    new StandardBus(busId, capacity)
            );
        }

        System.out.println("Bus registration request completed.");
    }

    // Add a stop
    private static void addStop() {

        System.out.println("\n--- ADD STOP ---");

        String stopId = readText("Enter stop ID: ");
        String stopName = readText("Enter stop name: ");

        if (stopId.isEmpty() || stopName.isEmpty()) {
            System.out.println("Stop ID and name cannot be empty.");
            return;
        }

        Stop stop = new Stop(stopId, stopName);

        roadNetwork.addStop(stop);
    }

    // Add a road between two stops
    private static void addRoad() {

        System.out.println("\n--- ADD ROAD ---");

        String source = readText("Enter source stop ID: ");

        String destination =
                readText("Enter destination stop ID: ");

        int travelTime =
                readInt("Enter travel time in minutes: ");

        roadNetwork.addRoad(
                source,
                destination,
                travelTime
        );
    }

    // Submit passenger demand
    private static void submitDemand() {

        System.out.println("\n--- SUBMIT PASSENGER DEMAND ---");

        String pickupId =
                readText("Enter pickup stop ID: ");

        String destinationId =
                readText("Enter destination stop ID: ");

        int passengerCount =
                readInt("Enter passenger count: ");

        if (passengerCount <= 0) {
            System.out.println(
                    "Passenger count must be greater than zero."
            );
            return;
        }

        Stop pickupStop = roadNetwork.getStop(pickupId);

        Stop destinationStop =
                roadNetwork.getStop(destinationId);

        if (pickupStop == null || destinationStop == null) {
            System.out.println(
                    "Invalid pickup or destination stop ID."
            );
            return;
        }

        if (pickupId.equals(destinationId)) {
            System.out.println(
                    "Pickup and destination must be different stops."
            );
            return;
        }

        String requestId = "REQ" + requestCounter++;

        DemandRequest request = new DemandRequest(
                requestId,
                pickupStop,
                destinationStop,
                passengerCount,
                arrivalCounter++
        );

        transportService.submitDemand(request);

        System.out.println(
                "Demand submitted successfully. Request ID: "
                        + requestId
        );
    }

    // Find a shortest route
    private static void findRoute() {

        System.out.println("\n--- FIND SHORTEST ROUTE ---");

        String source =
                readText("Enter starting stop ID: ");

        String destination =
                readText("Enter destination stop ID: ");

        displayRoute(source, destination);
    }

    // Display or recalculate a shortest route
    private static void displayShortestRoute() {

        System.out.println(
                "\n--- RECALCULATE SHORTEST ROUTE ---"
        );

        String source =
                readText("Enter starting stop ID: ");

        String destination =
                readText("Enter destination stop ID: ");

        displayRoute(source, destination);
    }

    // Shared route display logic
    private static void displayRoute(
            String source,
            String destination) {

        if (!roadNetwork.containsStop(source)
                || !roadNetwork.containsStop(destination)) {

            System.out.println(
                    "Starting stop or destination does not exist."
            );
            return;
        }

        List<String> route =
                routePlanner.findShortestRoute(
                        source,
                        destination
                );

        int travelTime =
                routePlanner.getTravelTime(
                        source,
                        destination
                );

        if (route.isEmpty() || travelTime < 0) {

            System.out.println(
                    "No route is available between these stops."
            );
            return;
        }

        System.out.println("Shortest route: " + route);
        System.out.println(
                "Estimated travel time: "
                        + travelTime + " minutes"
        );
    }

    // Complete a trip
    private static void completeTrip() {

        System.out.println("\n--- COMPLETE TRIP ---");

        String tripId = readText("Enter trip ID: ");

        transportService.completeTrip(tripId);
    }

    // Report a bus breakdown
    private static void breakdownBus() {

        System.out.println("\n--- BUS BREAKDOWN ---");

        String busId = readText("Enter bus ID: ");

        transportService.breakdownBus(busId);
    }

    // Repair a bus
    private static void repairBus() {

        System.out.println("\n--- REPAIR BUS ---");

        String busId = readText("Enter bus ID: ");

        transportService.repairBus(busId);
    }

    // Update traffic conditions on a road
    private static void updateTraffic() {

        System.out.println("\n--- UPDATE TRAFFIC ---");

        String source =
                readText("Enter source stop ID: ");

        String destination =
                readText("Enter destination stop ID: ");

        if (!roadNetwork.containsStop(source)
                || !roadNetwork.containsStop(destination)) {

            System.out.println(
                    "Starting stop or destination does not exist."
            );
            return;
        }

        int newTravelTime =
                readInt("Enter updated travel time in minutes: ");

        if (newTravelTime <= 0) {
            System.out.println(
                    "Travel time must be greater than zero."
            );
            return;
        }

        roadNetwork.addRoad(
                source,
                destination,
                newTravelTime
        );

        System.out.println(
                "Traffic travel time updated. "
                + "The next route calculation will use "
                + "the updated travel time."
        );
    }

    // Read an integer safely without using exception handling
    private static int readInt(String prompt) {

        while (true) {

            System.out.print(prompt);

            String input = scanner.nextLine().trim();

            if (input.matches("-?\\d+")) {

                try {
                    return Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println(
                            "Number is too large. Enter a smaller integer."
                    );
                }

            } else {
                System.out.println(
                        "Invalid input. Please enter a whole number."
                );
            }
        }
    }

    // Read text input
    private static String readText(String prompt) {

        System.out.print(prompt);

        return scanner.nextLine().trim();
    }
}
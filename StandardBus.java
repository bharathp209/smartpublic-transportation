public class StandardBus extends Bus {

    public StandardBus(String busId, int capacity) {
        super(busId, capacity);
    }

    public boolean canUseRoad(int travelTime) {
        return travelTime > 0;
    }
}
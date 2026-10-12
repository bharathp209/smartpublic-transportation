public class MiniBus extends Bus {

    public MiniBus(String busId, int capacity) {
        super(busId, capacity);
    }

    public boolean canUseRoad(int travelTime) {
        return travelTime > 0;
    }
}
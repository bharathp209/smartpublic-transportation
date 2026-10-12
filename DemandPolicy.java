import java.util.Comparator;

public class DemandPolicy {

    public Comparator<DemandRequest> getPriorityComparator() {

        return Comparator
                .comparingInt(DemandRequest::getPassengerCount)
                .reversed()
                .thenComparingInt(DemandRequest::getArrivalOrder);
    }
}
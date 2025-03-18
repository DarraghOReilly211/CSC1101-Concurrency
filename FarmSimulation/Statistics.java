import java.util.*;

public class Statistics {
    private static final List<Integer> buyerWaitTimes = new ArrayList<>();
    private static final List<Integer> farmerWorkTimes = new ArrayList<>();

    public static synchronized void logBuyerWaitTime(int ticks) {
        buyerWaitTimes.add(ticks);
    }

    public static synchronized void logFarmerWorkTime(int ticks) {
        farmerWorkTimes.add(ticks);
    }

    public static void printStatistics() {
        synchronized (Statistics.class) {
            double avgBuyerWait = buyerWaitTimes.stream().mapToInt(Integer::intValue).average().orElse(0);
            double avgFarmerWork = farmerWorkTimes.stream().mapToInt(Integer::intValue).average().orElse(0);

            System.out.println("\n===== Performance Statistics =====");
            System.out.println("(Ticks :" + Clock.getTickCount() + ") " + "Average Buyer Wait Time: " + avgBuyerWait + " ticks");
            System.out.println("(Ticks :" + Clock.getTickCount() + ") " + "Average Farmer Work Time: " + avgFarmerWork + " ticks");
            System.out.println("===================================\n");
        }
    }
}

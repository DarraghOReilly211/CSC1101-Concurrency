public class Clock {
    private static final long startTime = System.currentTimeMillis();

    public static long getTickCount() {
        return (System.currentTimeMillis() - startTime) / Constants.TICK_DURATION_MS;
    }
}

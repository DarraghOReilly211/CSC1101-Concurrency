public class LoggingThread extends Thread {
    public LoggingThread() {
        this.setName("Logging Thread");
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(Constants.LOGGING_INTERVAL);
                Statistics.printStatistics();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

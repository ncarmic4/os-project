import java.io.IOException;
import java.util.concurrent.*;

/**
 * Main OS Execution Driver.
 */
public class Driver {

    // CONFIGURATION
    static int ramSize = 1024;
    static int diskSize = 2048;
    static int cacheSize = 128;
    static int jobCount = 30;
    static int msThreadDelay = 0;

    public static void main(String[] args) throws InterruptedException, IOException, BrokenBarrierException {
        MetricCollector.printInfo();
        exec(1, Scheduler.SchedulerMode.FIFO);
        exec(4, Scheduler.SchedulerMode.FIFO);
        exec(1, Scheduler.SchedulerMode.PRIORITY);
        exec(4, Scheduler.SchedulerMode.PRIORITY);
    }

    /**
     * Reset the simulation for another run.
     * @param mode The mode of the scheduler (FIFO vs PRIORITY).
     */
    static void reset(Scheduler.SchedulerMode mode) {
        Scheduler.cpuList.clear();
        Scheduler.jobList.clear();
        Scheduler.mode = mode;
        Loader.load();
        MMU.init();
    }

    /**
     * Execution method of a run of the simulation.
     * Starts all threads and initializes components.
     * @param cores Number of cores to run the simulation with.
     * @param mode The mode of the scheduler (FIFO vs PRIORITY).
     * @throws IOException When MetricCollector cannot write to files.
     * @throws InterruptedException When ExecutorService cannot execute threads.
     */
    static void exec(int cores, Scheduler.SchedulerMode mode) throws IOException, InterruptedException {
        // Reset simulation
        reset(mode);

        // Initialize MetricCollector to a new file
        MetricCollector.init(mode.toString().toLowerCase() + "-" + cores + "-core.csv");

        // Create CPU threads without starting
        for (int i = 0; i < cores; i++) {
            CPU cpu = new CPU(i);
            cpu.setName("CPU " + i);
            Scheduler.addCpu(cpu);
        }

        // Use ExecutorService to start threads all at once
        ExecutorService executorService = Executors.newCachedThreadPool();
        MetricCollector.setGlobalStartTime(System.currentTimeMillis());
        for (CPU cpu : Scheduler.cpuList) {
            executorService.execute(cpu);
        }

        // Wait for all threads to complete
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.MINUTES);
        MetricCollector.printAllMetrics();
    }
}

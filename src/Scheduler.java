import java.util.*;

/**
 * Schedules jobs based on priority.
 */
public class Scheduler {

    public static SchedulerMode mode = SchedulerMode.PRIORITY;

    public static final ArrayList<PCB> jobList = new ArrayList<>();
    public static ArrayList<CPU> cpuList = new ArrayList<>();

    private static final PriorityQueue<PCB> priorityQueue = new PriorityQueue<>();
    private static final LinkedList<PCB> fifoQueue = new LinkedList<>();

    /**
     * Add a job to the priority queue and list of jobs.
     * @param job The job to be added.
     */
    static void addJob(PCB job) {
        job.setAddedTime(System.currentTimeMillis());
        jobList.add(job);
        if (mode == SchedulerMode.PRIORITY) {
            priorityQueue.add(job);
        } else {
            fifoQueue.add(job);
        }
    }

    static void addCpu(CPU cpu) {
        cpuList.add(cpu);
    }

    static synchronized boolean hasNext() {
        PCB nextJob;
        if (mode == SchedulerMode.PRIORITY) {
            nextJob = priorityQueue.peek();
        } else {
            nextJob = fifoQueue.peek();
        }
        return nextJob == null;
    }

    /**
     * Load the next job from disk into RAM.
     */
    static synchronized PCB nextJob() {
        PCB nextJob;
        if (mode == SchedulerMode.PRIORITY) {
            nextJob = priorityQueue.poll();
        } else {
            nextJob = fifoQueue.poll();
        }

        if (nextJob != null) {
            nextJob.setJobState(PCB.JobState.RUNNING);
        }
        return nextJob;
    }

    static void signalInterrupt(CPU cpu) {
        cpu.setHasInterrupt(true);
    }

    static void handleInterrupt(CPU cpu) {
        PCB blockedJob = cpu.getCurrentJob();
        blockedJob.setJobState(PCB.JobState.BLOCKED);
        addJob(blockedJob);
    }

    public enum SchedulerMode {
        FIFO,
        PRIORITY
    }
}

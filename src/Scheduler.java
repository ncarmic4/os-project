import java.util.*;

/**
 * Schedules jobs based on priority.
 */
public class Scheduler {

    public static SchedulerMode mode;

    public static final ArrayList<PCB> jobList = new ArrayList<>();
    public static ArrayList<CPU> cpuList = new ArrayList<>();

    private static final PriorityQueue<PCB> priorityQueue = new PriorityQueue<>();
    private static final LinkedList<PCB> fifoQueue = new LinkedList<>();

    /**
     * Add a job to either the fifo or priority queue.
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

    /**
     * Add a CPU to the list of CPUs.
     * @param cpu The CPU to be added.
     */
    static void addCpu(CPU cpu) {
        cpuList.add(cpu);
    }

    /**
     * Determine if there is a remaining job in the queue.
     * Synchronized to avoid a race condition between multiple CPUs.
     * @return True if there is a remaining job.
     */
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
     * Return the next job in the queue.
     * Synchronized to avoid a race condition between multiple CPUs.
     * @return The PCB object of the next job.
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

    /**
     * Signal an interrupt on a CPU.
     * @param cpu The CPU to be interrupted.
     */
    static void signalInterrupt(CPU cpu) {
        cpu.setHasInterrupt(true);
    }

    /**
     * Handle an interrupt on a CPU. Transitions the running job to a blocked state, and add it back to the queue.
     * @param job The Job that is interrupted.
     */
    static void handleInterrupt(PCB job) {
        job.setJobState(PCB.JobState.BLOCKED);
        addJob(job);
    }

    /**
     * An enum that holds possible scheduler modes.
     */
    public enum SchedulerMode {
        FIFO,
        PRIORITY
    }
}

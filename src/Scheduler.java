import java.util.*;

/**
 * Schedules jobs based on priority.
 */
public class Scheduler {

    public static int mode = 0; // 0 for priority, 1 for FIFO

    private static final PriorityQueue<PCB> priorityQueue = new PriorityQueue<>();
    private static final LinkedList<PCB> fifoQueue = new LinkedList<>();

    /**
     * Add a job to the priority queue and list of jobs.
     * @param job The job to be added.
     */
    static void addJob(PCB job) {
        job.setAddedTime(System.currentTimeMillis());
        if (mode == 0) {
            priorityQueue.add(job);
        } else {
            fifoQueue.add(job);
        }
    }

    /**
     * Load the next job from disk into RAM.
     */
    static PCB nextJob() {
        PCB nextJob;
        if (mode == 0) {
            nextJob = priorityQueue.poll();
        } else {
            nextJob = fifoQueue.poll();
        }

        if (nextJob != null) {
            nextJob.setJobState("running");
            System.out.println(nextJob);
            nextJob.setStartTime(System.currentTimeMillis());
        }
        return nextJob;
    }

    /**
     * Check if all jobs have finished executing.
     * @return true if no jobs left.
     */
    static boolean allJobsDone() {
        if (mode == 0) {
            return priorityQueue.size() == 0;
        } else {
            return fifoQueue.size() == 0;
        }
    }

    static void signalInterrupt(CPU cpu) {
        cpu.setHasInterrupt(true);
    }

    static void handleInterrupt(CPU cpu) {
        PCB blockedJob = cpu.getCurrentJob();
        blockedJob.setJobState("blocked");
        addJob(blockedJob);
    }
}

import java.util.ArrayList;

/**
 * Schedules jobs based on priority.
 */
public class Scheduler {
    // TODO: implement a priority queue

    ArrayList<PCB> jobList = new ArrayList<>();

    /**
     * Add a job to the priority queue and list of jobs.
     * @param job The job to be added.
     */
    public void addJob(PCB job) {
        jobList.add(job);
        // TODO: Implement adding to queue
    }

    /**
     * Load the next job from disk into RAM.
     */
    public PCB nextJob() {
        // TODO: implement nextJob method
        return null;
    }

    /**
     * Check if all jobs have finished executing.
     * @return true if no jobs left.
     */
    public boolean allJobsDone() {
        // TODO: implement allJobsDone method
        return false;
    }

    /**
     * Check for pending interrupts.
     */
    public void waitForInterrupt() {
        // TODO: implement waitForInterrupt method (might be in wrong class)
    }

    /**
     * A simple debug job list method. TODO: remove this
     */
    public void listJobs() {
        for (PCB job : jobList) {
            System.out.println("jobId=" + job.jobId + " | priority=" + job.priority
                    + " | numInstructions=" + job.numInstructions);
        }
    }
}

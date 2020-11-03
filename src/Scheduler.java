import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Schedules jobs based on priority.
 */
public class Scheduler {

    int counter = 0;
    HashMap<Integer, PCB> jobList = new HashMap<>();
    ArrayList<PCB> sortedList = new ArrayList<>();

    /**
     * Add a job to the priority queue and list of jobs.
     * @param job The job to be added.
     */
    public void addJob(PCB job) {
        System.out.println(job.jobId);
        jobList.put(job.getPriority(), job);
    }

    public void sortList() {
        TreeMap<Integer, PCB> treeMap = new TreeMap<>(jobList);
        for (Map.Entry<Integer, PCB> entry : treeMap.entrySet()) {
            System.out.println(entry.getKey() + " | " + entry.getValue().jobId);
            sortedList.add(entry.getValue());
        }
    }

    /**
     * Load the next job from disk into RAM.
     */
    public PCB nextJob() {
        PCB job = sortedList.get(counter);
        counter++;
        return job;
    }

    /**
     * Check if all jobs have finished executing.
     * @return true if no jobs left.
     */
    public boolean allJobsDone() {
        return counter == sortedList.size() - 1;
    }

    /**
     * Check for pending interrupts.
     */
    public void waitForInterrupt() {
        // TODO: implement waitForInterrupt method (might be in wrong class)
    }
}

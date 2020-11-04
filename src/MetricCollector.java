import java.util.ArrayList;

public class MetricCollector {
    static ArrayList<PCB> finishedJobs = new ArrayList<>();

    static void addJob(PCB job) {
        finishedJobs.add(job);
    }

    static void listTimes() {
        for (PCB job : finishedJobs) {
            System.out.println("Job " + job.getJobId() + " | Waiting time: " + (job.getStartTime() - job.getAddedTime())
                + " | Completion time: " + (job.getCompletedTime() - job.getStartTime()));
        }
    }

}

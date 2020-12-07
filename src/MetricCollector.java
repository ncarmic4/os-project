import java.io.*;

public class MetricCollector {

    static BufferedWriter bufferedWriter;
    static long globalStartTime;

    /**
     * Initialize writer to a new file for the simulation.
     * @param filename Name of the file.
     * @throws IOException When file cannot be created.
     */
    static void init(String filename) throws IOException {
        File metrics = new File("./metrics/" + filename);
        bufferedWriter = new BufferedWriter(new PrintWriter(metrics));
    }

    /**
     * Print information regarding data values to a readme file.
     * @throws IOException When file cannot be written to.
     */
    static void printInfo() throws IOException {
        init("README.md");
        bufferedWriter.write("# JOB COMPLETION METRICS\n");
        bufferedWriter.write("Job ID: ID number of the job.\n");
        bufferedWriter.write("CPU ID: ID number of the cpu that ran the job.\n");
        bufferedWriter.write("Waiting Time: Time in seconds that the job was waiting to be run.\n");
        bufferedWriter.write("Completion Time: Time in seconds that the job took to complete.\n");
        bufferedWriter.write("I/O Processes: Number of I/O processes that the job makes during its lifespan.\n");
        bufferedWriter.write("MMU RAM % Used: At the time the job is run, percentage of RAM used by all jobs.\n");
        bufferedWriter.write("Job RAM % Used: The percentage of RAM used by this job.\n");
        bufferedWriter.write("Job Cache % Used: The percentage of cache of the assigned cpu used by this job.\n");
        bufferedWriter.newLine();
        bufferedWriter.write("# CPU COMPLETION METRICS\n");
        bufferedWriter.write("CPU ID: ID number of the cpu that ran the job.\n");
        bufferedWriter.write("Completion Time: Time in seconds that the cpu took to complete all assigned jobs.\n");
        bufferedWriter.write("I/O Processes: Number of I/O processes that the cpu makes during its lifespan.\n");
        bufferedWriter.write("Number of Jobs: Total number of jobs the CPU runs during its lifespan.\n");
        bufferedWriter.write("% of Jobs: Percentage of jobs the CPU runs out of the total job pool.\n");
        close();
    }

    /**
     * Print all Simulation Metrics to the file.
     * @throws IOException When file cannot be written to.
     */
    static void printAllMetrics() throws IOException {
        listJobMetrics();
        bufferedWriter.newLine();
        listCpuMetrics();
        close();
    }

    /**
     * Write the individual Job Metrics to the file.
     * @throws IOException When file cannot be written to.
     */
    static void listJobMetrics() throws IOException {
        bufferedWriter.write("# JOB COMPLETION METRICS\n");
        bufferedWriter.write("Job ID,CPU ID,Waiting Time,Completion Time,I/O Processes,MMU RAM % Used,Job RAM % Used,Job Cache % Used\n");
        for (PCB job : Scheduler.jobList) {
            long waitingTime = job.getStartTime() - globalStartTime;
            double mmuPercentRam = (double) Math.round((double) job.getRamUsage() / Driver.ramSize * 1000) / 1000;
            double jobPercentRam = (double) Math.round((double) job.getTotalSize() / Driver.ramSize * 1000) / 1000;
            double jobPercentCache = (double) Math.round((double) job.getCacheUsage() / Driver.cacheSize * 1000) / 1000;
            bufferedWriter.write(job.getJobId() + "," + job.getCurrentCpu().getCpuId() + "," + waitingTime + "," +
                    job.getCompletionTime() + "," + job.getNumIoProcesses() + "," + mmuPercentRam + "," + jobPercentRam
                    + "," + jobPercentCache + "\n");
        }
    }

    /**
     * Write the CPU Metrics to the file.
     * @throws IOException When file cannot be written to.
     */
    static void listCpuMetrics() throws IOException {
        bufferedWriter.write("# CPU COMPLETION METRICS\n");
        bufferedWriter.write("CPU ID,Completion Time,I/O Processes,Number of Jobs,% of Jobs\n");
        for (CPU cpu : Scheduler.cpuList) {
            double percentJobs = (double) Math.round((double) cpu.getJobCount() / Driver.jobCount * 1000) / 1000;
            bufferedWriter.write(cpu.getCpuId() + "," + cpu.getCompletionTime() + "," + cpu.getIoProcesses()
                    + "," + cpu.getJobCount() + "," + percentJobs + "\n");
        }
    }

    /**
     * Close the buffered writer.
     * @throws IOException When the writer fails to close.
     */
    static void close() throws IOException {
        bufferedWriter.close();
    }

    static void setGlobalStartTime(long time) {
        globalStartTime = time;
    }

    static void coreDumpRAM() throws IOException {
        for(String s : MMU.ram) {
            bufferedWriter.write(s + "\n");
        }
    }
}

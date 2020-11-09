import java.util.ArrayList;

/**
 * Process Control Block.
 */
public class PCB implements Comparable<PCB> {

    // JOB INFORMATION
    private final int jobId;
    private int pc;
    private final int priority;

    // JOB STATE
    private JobState jobState;
    private CPU currentCpu;
    private Register[] registers = new Register[16];

    // INSTRUCTION INFORMATION
    private final int numInstructions;
    private int inputBufferSize;
    private int outputBufferSize;
    private int tempBufferSize;

    // DISK POINTERS
    private final int diskStart;

    // RAM POINTERS
    private int ramStart;
    private int ramEnd;

    // METRICS
    private long addedTime;
    private long startTime;
    private long completionTime;
    private int ramUsage;
    private int cacheUsage;

    PCB(String jobId, String numInstructions, String priority, int diskStart) {
        this.jobId = Integer.parseInt(jobId, 16);
        this.pc = 0;
        this.numInstructions = Integer.parseInt(numInstructions, 16);
        this.priority = Integer.parseInt(priority, 16);
        this.diskStart = diskStart;
        this.jobState = JobState.NEW;

        // Initialize Registers
        for (int i = 0; i < this.registers.length; i++) {
            this.registers[i] = new Register();
        }
    }

    // Getters/setters
    int getJobId() {
        return jobId;
    }
    int getProgramCounter() {
        return pc;
    }
    void incrementProgramCounter() {
        pc++;
    }
    int getPriority() {
        return priority;
    }
    JobState getJobState() {
        return jobState;
    }
    void setJobState(JobState jobState) {
        this.jobState = jobState;
    }
    void setCurrentCpu(CPU cpu) {
        this.currentCpu = cpu;
    }
    CPU getCurrentCpu() {
        return currentCpu;
    }
    Register[] getRegisters() {
        return registers;
    }
    void setRegisters(Register[] registers) {
        this.registers = registers;
    }
    int getNumInstructions() {
        return numInstructions;
    }
    void setInputBufferSize(int inputBufferSize) {
        this.inputBufferSize = inputBufferSize;
    }
    void setOutputBufferSize(int outputBufferSize) {
        this.outputBufferSize = outputBufferSize;
    }
    void setTempBufferSize(int tempBufferSize) {
        this.tempBufferSize = tempBufferSize;
    }
    int getTotalSize() {
        return numInstructions + inputBufferSize + outputBufferSize + tempBufferSize;
    }
    int getDiskStart() {
        return diskStart;
    }
    int getRamStart() {
        return ramStart;
    }
    void setRamStart(int index) {
        this.ramStart = index;
    }
    int getRamEnd() {
        return ramEnd;
    }
    void setRamEnd(int index) {
        this.ramEnd = index;
    }

    // METRICS
    void setAddedTime(long time) {
        this.addedTime = time;
    }
    void setStartTime(long time) {
        this.startTime = time;
    }
    void setCompletionTime(long time) {
        this.completionTime = time;
    }
    long getStartTime() {
        return startTime;
    }
    long getCompletionTime() {
        return completionTime - startTime;
    }
    void setRamUsage(int usage) {
        this.ramUsage = usage;
    }
    void setCacheUsage(int usage) {
        this.cacheUsage = usage;
    }
    int getRamUsage() {
        return ramUsage;
    }
    int getCacheUsage() {
        return cacheUsage;
    }

    /**
     * An enum that holds possible job state values.
     */
    public enum JobState {
        BLOCKED,
        READY,
        NEW,
        RUNNING;
    }

    /**
     * A comparable override method that holds instructions for comparing two PCB objects.
     * This is primarily used by the implementation of the Scheduler's priority queue.
     * First, jobs are ranked by the current state, with blocked jobs having the highest priority.
     * Jobs of similar ranking are secondly sorted by their priority value that is contained in the job control cards.
     * Finally, jobs with the state and priority are ranked by their number of instructions, with the least number
     * taking priority.
     * @param pcb The job to compare against this job.
     * @return (See below)
     *      -1 if job x has a lower priority than job y
     *      0 if job x has an equal priority to job y
     *      1 if job x has a higher priority than job y
     */
    @Override
    public int compareTo(PCB pcb) {
        int stateCompare = jobState.compareTo(pcb.getJobState());
        int priorityCompare = Integer.compare(priority, pcb.getPriority());
        int numInstrCompare = Integer.compare(numInstructions, pcb.numInstructions);

        // Order is selected based on state, then priority, then number of instructions
        if (stateCompare == 0) {
            if (priorityCompare == 0) {
                return numInstrCompare;
            } else {
                return priorityCompare;
            }
        } else {
            return stateCompare;
        }
    }

    @Override
    public String toString() {
        ArrayList<String> info = new ArrayList<>();
        info.add("ID: " + jobId);
        info.add("Priority: " + priority);
        info.add("numInstr: " + numInstructions);
        info.add("State: " + jobState);
        info.add("RAM: " + ramStart + "-" + ramEnd);
        info.add("cpu: " + currentCpu);

        StringBuilder finalOutput = new StringBuilder();
        finalOutput.append("| ");
        for (String s : info) {
            while (s.length() < 15) {
                s = s + " ";
            }
            finalOutput.append(s).append(" | ");
        }

        return finalOutput.toString();
    }
}

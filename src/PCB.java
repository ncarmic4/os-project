import java.util.Date;

/**
 * Process Control Block.
 */
public class PCB implements Comparable<PCB> {
    private int pc;
    private final int jobId;
    private final int numInstructions;
    private final int priority;
    private CPU currentCpu;

    private Register[] registers = new Register[16];

    private final int start;
    private int inputBufferSize, outputBufferSize, tempBufferSize;
    private int ramStart, ramEnd; // start is inclusive, end is exclusive

    // Assign Job state to numeric value for comparisons in PriorityQueue
    public static final String[] jobStates = new String[] {"running", "ready", "blocked", "new"};
    private int jobState;


    // METRICS
    private long addedTime, startTime, completedTime;

    PCB(String jobId, String numInstructions, String priority, int start) {
        this.jobId = Integer.parseInt(jobId, 16);
        this.pc = 0;
        this.numInstructions = Integer.parseInt(numInstructions, 16);
        this.priority = Integer.parseInt(priority, 16);
        this.start = start;
        this.jobState = 3; // set state = new

        for (int i = 0; i < this.registers.length; i++) {
            this.registers[i] = new Register();
        }
    }

    int getNumInstructions() {
        return numInstructions;
    }
    int getPriority() {
        return priority;
    }
    int getStart() {
        return start;
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
    int getDataSize() {
        return inputBufferSize + outputBufferSize + tempBufferSize;
    }
    String getJobStateString() {
        return jobStates[jobState];
    }
    int getJobStateInt() {
        return jobState + 1;
    }
    void setJobState(String jobState) {
        for (int i = 0; i < jobStates.length; i++) {
            if (jobStates[i].equals(jobState)) {
                this.jobState = i;
                return;
            }
        }
    }
    void setCurrentCpu(CPU cpu) {
        this.currentCpu = cpu;
    }
    void incrementProgramCounter() {
        pc++;
    }
    int getProgramCounter() {
        return pc;
    }
    void setRamStart(int index) {
        this.ramStart = index;
    }
    void setRamEnd(int index) {
        this.ramEnd = index;
    }
    int getRamStart() {
        return ramStart;
    }
    int getRamEnd() {
        return ramEnd;
    }
    Register[] getRegisters() {
        return registers;
    }
    void setRegisters(Register[] registers) {
        this.registers = registers;
    }
    int getJobId() {
        return jobId;
    }

    // METRICS
    void setAddedTime(long time) {
        this.addedTime = time;
    }
    void setStartTime(long time) {
        this.startTime = time;
    }
    void setCompletedTime(long time) {
        this.completedTime = time;
    }
    long getAddedTime() {
        return addedTime;
    }
    long getStartTime() {
        return startTime;
    }
    long getCompletedTime() {
        return completedTime;
    }

    @Override
    public int compareTo(PCB pcb) {
        int stateCompare = Integer.compare(getJobStateInt(), pcb.getJobStateInt());
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
        return "ID: " + jobId + " | Priority: " + priority + " | numInstr: " + numInstructions + " | State: " + getJobStateString()
                + " | RAM: " + ramStart + "-" + ramEnd + " | cpu: " + currentCpu;
    }
}

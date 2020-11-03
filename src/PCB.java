/**
 * Process Control Block.
 */
public class PCB {
    int cpuId;
    int jobId;
    int pc;
    int numInstructions;
    int priority;



    int firstInstruction;
    int inputBufferSize, outputBufferSize, tempBufferSize;

    Registers[] registers;

    // TODO: missing lots of vars, check CPU phase 1

    public PCB(String jobId, String numInstructions, String priority, int firstInstruction) {
        this.jobId = Integer.parseInt(jobId, 16);
        this.numInstructions = Integer.parseInt(numInstructions, 16);
        this.priority = Integer.parseInt(priority, 16);
        this.firstInstruction = firstInstruction;

        // Initialize registers TODO: set dynamic register length
        this.registers = new Registers[16];
        for (int i = 0; i < this.registers.length; i++) {
            this.registers[i] = new Registers();
        }
    }

    public void setInputBufferSize(int inputBufferSize) {
        this.inputBufferSize = inputBufferSize;
    }

    public void setOutputBufferSize(int outputBufferSize) {
        this.outputBufferSize = outputBufferSize;
    }

    public void setTempBufferSize(int tempBufferSize) {
        this.tempBufferSize = tempBufferSize;
    }

    public int getNumInstructions() {
        return numInstructions;
    }

    public int getPriority() {
        return priority;
    }

    public int getFirstInstruction() {
        return firstInstruction;
    }

    public Registers[] getRegisters() {
        return registers;
    }
}

/**
 * Process Control Block.
 */
public class PCB {
    int cpuId, jobId, pc, numInstructions, priority;
    int inputBufferSize, outputBufferSize, tempBufferSize;

    Registers[] registers;

    // TODO: missing lots of vars, check CPU phase 1

    public PCB(String jobId, String numInstructions, String priority) {
        this.jobId = Integer.parseInt(jobId, 16);
        this.numInstructions = Integer.parseInt(numInstructions, 16);
        this.priority = Integer.parseInt(priority, 16);

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

    public Registers[] getRegisters() {
        return registers;
    }
}

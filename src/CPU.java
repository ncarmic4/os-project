import java.math.BigInteger;
import java.util.Arrays;

/**
 * Core Processing Unit.
 * A class to handle execution of instructions read from memory.
 */
public class CPU extends Thread {

    // CPU Identification Info
    private PCB currentJob;
    private final int cpuId;
    private CpuState cpuState;

    // Determine opcode by indexing opcodeArray
    private final String[] opcodeArray = {"RD", "WR", "ST", "LW", "MOV", "ADD", "SUB", "MUL", "DIV", "AND",
            "OR", "MOVI", "ADDI", "MULI", "DIVI", "LDI", "SLT", "SLTI", "HLT", "NOP", "JMP", "BEQ",
            "BNE", "BEZ", "BNZ", "BGZ", "BLZ"};

    // Properties of binary instructions
    private String binary;
    private String address;
    private int reg1Index;
    private int reg2Index;
    private int reg3Index;
    private int addressIndex;

    // Memory
    private Register[] registers = new Register[16];
    private final String[] cache = new String[Driver.cacheSize];

    // Program continuation variables
    private int pc;
    private boolean continueExecution = true;
    private boolean hasInterrupt = false;

    // METRICS
    private final long startTime;
    private long completionTime;
    private int ioProcesses = 0;
    private int jobCount;

    public CPU (int id) {
        this.startTime = System.currentTimeMillis();
        this.cpuId = id;
        this.cpuState = CpuState.FREE;
    }

    /**
     * A wrapper method to load an instruction at a specified address from RAM.
     * @param index Address of the instruction.
     * @return String value of the instruction.
     */
    private String fetch(int index) {
        return cache[index].substring(0, 8);
    }

    /**
     * Decode a binary string of instructions.
     * Chars 0-1 indicate instruction format.
     * Chars 2-7 indicate instruction OPCODE.
     * Chars 8-32 indicate additional registers, addresses, etc.
     * @param hex Instruction string in hex format.
     */
    private void decode(String hex) {
        StringBuilder binaryStr = new StringBuilder();
        binaryStr.append(new BigInteger(hex, 16).toString(2));
        // Adds leading zeros if binary string is less than 32 chars long
        while (binaryStr.toString().length() < 32) {
            binaryStr.insert(0, "0");
        }
        binary = binaryStr.toString();

        // Chars 0-1 indicate type of instruction (arithmetic, conditional, etc)
        instructionFormat(binary.substring(0, 2));

        // Chars 2-7 specify opcode of action. This is converted to decimal and used to index opcodeArray
        String opcodeBinary = binary.substring(2, 8);
        evaluate(opcodeArray[Integer.parseInt(opcodeBinary, 2)]);
    }

    /**
     * Main thread execution of the CPU class.
     * Each CPU will independently check for remaining jobs and execute them accordingly.
     */
    @Override
    public void run() {

        // TODO: interrupt handling
        //  check if jobs complete successfully

        while (!Scheduler.hasNext()) {
            PCB nextJob = Scheduler.nextJob();
            if (nextJob != null) {
                jobCount++;
                cpuState = CpuState.EXECUTING;
                Dispatcher.loadJob(nextJob, this);
                nextJob.setRamUsage(MMU.getRamUsage());
                loadInstructionsToCache();
                nextJob.setCacheUsage(getCacheUsage());
                while (continueExecution && pc < currentJob.getNumInstructions()) {
                    // Artificial exec time for each instruction
                    try {
                        Thread.sleep(Driver.msThreadDelay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String hex = fetch(pc);
                    pc++;
                    currentJob.incrementProgramCounter();
                    decode(hex);
                }
                MMU.clearBits(currentJob.getRamStart(), currentJob.getRamEnd());
                Dispatcher.unloadJob(currentJob, this);
                cpuState = CpuState.FREE;
                clearCache();
            }
        }

        completionTime = System.currentTimeMillis();
    }

    // Getters/setters
    void resetProgramCounter() {
        this.pc = 0;
        this.continueExecution = true;
    }
    void setCurrentJob(PCB job) {
        this.currentJob = job;
    }
    PCB getCurrentJob() {
        return currentJob;
    }
    void setHasInterrupt(boolean interrupt) {
        this.hasInterrupt = interrupt;
    }
    void setRegisters(Register[] registers) {
        this.registers = registers;
    }
    Register[] getRegisters() {
        return registers;
    }
    public int getCpuId() {
        return cpuId;
    }
    public long getCompletionTime() {
        return completionTime - startTime;
    }
    public int getIoProcesses() {
        return ioProcesses;
    }
    public int getJobCount() {
        return jobCount;
    }

    /**
     * Clear the cache array when the job is unloaded.
     */
    void clearCache() {
        Arrays.fill(cache, "");
    }

    /**
     * Read instructions for the current job from RAM into cache.
     */
    void loadInstructionsToCache() {
        for (int i = 0; i < currentJob.getTotalSize(); i++) {
            cache[i] = MMU.loadRam(currentJob.getRamStart() + i);
        }
    }

    /**
     * Get the number of currently loaded instructions in the cache.
     * @return number of loaded instructions.
     */
    public int getCacheUsage() {
        int usage = 0;
        for (String s : cache) {
            if (s != null && !s.equals("")) {
                usage++;
            }
        }
        return usage;
    }

    /**
     * Method to initialize register indexes based on instruction format.
     * These are predefined, so for example arithmetic instructions specify reg1
     * in chars 8-11, reg2 in chars 12-15, and reg3 in chars 16-19.
     * @param format First two bits of binary instruction that specify type of instruction.
     */
    private void instructionFormat(String format) {
        switch (format) {
            case "00": { // ARITHMETIC
                String reg1 = binary.substring(8, 12);
                reg1Index = Integer.parseInt(reg1, 2);

                String reg2 = binary.substring(12, 16);
                reg2Index = Integer.parseInt(reg2, 2);

                String reg3 = binary.substring(16, 20);
                reg3Index = Integer.parseInt(reg3, 2);
                break;
            }
            case "01": // CONDITIONAL
            case "11": { // INPUT/OUTPUT
                    String reg1 = binary.substring(8, 12);
                reg1Index = Integer.parseInt(reg1, 2);

                String reg2 = binary.substring(12, 16);
                reg2Index = Integer.parseInt(reg2, 2);

                address = binary.substring(16, 32);
                addressIndex = Integer.parseInt(address, 2) / 4;
                break;
            }
            case "10": { // UNCONDITIONAL
                address = binary.substring(8, 32);
                addressIndex = Integer.parseInt(address, 2) / 4;
                break;
            }
        }
    }

    /**
     * Method to provide operations for each OPCODE.
     * @param opcode OPCODE string.
     */
    private void evaluate(String opcode) {
        switch (opcode) {
            case "RD": {
                if (addressIndex == 0) {
                    registers[reg1Index].data = Integer.parseInt(cache[registers[reg2Index].data], 16);
                } else {
                    registers[reg1Index].data = Integer.parseInt(cache[addressIndex], 16);
                }
                ioProcesses++;
                currentJob.incrementIoProcesses();
                break;
            }
            case "WR": {
                cache[addressIndex] = Integer.toHexString(registers[reg1Index].data);
                ioProcesses++;
                currentJob.incrementIoProcesses();
                break;
            }
            case "ST": {
                if (addressIndex == 0) {
                    cache[registers[reg2Index].data] = Integer.toHexString(registers[reg1Index].data);
                } else {
                    cache[addressIndex] = Integer.toHexString(registers[reg1Index].data);
                }
                break;
            }
            case "LW": {
                if (addressIndex == 0) {
                    registers[reg2Index].data = Integer.parseInt(cache[registers[reg1Index].data], 16);
                } else {
                    registers[reg2Index].data = Integer.parseInt(cache[addressIndex], 16);
                }
                break;
            }
            case "MOV": {
                registers[reg3Index].data = registers[reg1Index].data;
                break;
            }
            case "ADD": {
                registers[reg3Index].data = registers[reg1Index].data + registers[reg2Index].data;
                break;
            }
            case "SUB": {
                registers[reg3Index].data = registers[reg1Index].data - registers[reg2Index].data;
                break;
            }
            case "MUL": {
                registers[reg3Index].data = registers[reg1Index].data * registers[reg2Index].data;
                break;
            }
            case "DIV": {
                if (registers[reg2Index].data != 0) {
                    registers[reg3Index].data = registers[reg1Index].data / registers[reg2Index].data;
                }
                break;
            }
            case "AND": {
                if (registers[reg1Index].data != 0 && registers[reg2Index].data != 0) {
                    registers[reg3Index].data = 1;
                } else {
                    registers[reg3Index].data = 0;
                }
                break;
            }
            case "OR": {
                if (registers[reg1Index].data == 1 || registers[reg2Index].data == 1) {
                    registers[reg3Index].data = 1;
                } else {
                    registers[reg3Index].data = 0;
                }
                break;
            }
            case "MOVI": {
                registers[reg2Index].data = Integer.parseInt(address, 2);
                break;
            }
            case "ADDI": {
                registers[reg2Index].data++;
                break;
            }
            case "MULI": {
                registers[reg2Index].data = registers[reg2Index].data * addressIndex;
                break;
            }
            case "DIVI": {
                if (addressIndex != 0) {
                    registers[reg2Index].data = registers[reg2Index].data / addressIndex;
                }
                break;
            }
            case "LDI": {
                registers[reg2Index].data = addressIndex;
                break;
            }
            case "SLT": {
                if (registers[reg1Index].data < registers[reg2Index].data) {
                    registers[reg3Index].data = 1;
                } else {
                    registers[reg3Index].data = 0;
                }
                break;
            }
            case "SLTI": {
                if (registers[reg1Index].data < addressIndex) {
                    registers[reg2Index].data = 1;
                } else {
                    registers[reg2Index].data = 0;
                }
                break;
            }
            case "HLT": {
                continueExecution = false;
                break;
            }
            case "NOP": {
                pc++;
                break;
            }
            case "JMP": {
                pc = addressIndex;
                break;
            }
            case "BEQ": {
                if (registers[reg1Index].data == registers[reg2Index].data) {
                    pc = addressIndex;
                }
                break;
            }
            case "BNE": {
                if (registers[reg1Index].data != registers[reg2Index].data) {
                    pc = addressIndex;
                }
                break;
            }
            case "BEZ": {
                if (registers[reg2Index].data == 0) {
                    pc = addressIndex;
                }
                break;
            }
            case "BNZ": {
                if (registers[reg1Index].data != 0) {
                    pc = addressIndex;
                }
                break;
            }
            case "BGZ": {
                if (registers[reg1Index].data > 0) {
                    pc = addressIndex;
                }
                break;
            }
            case "BLZ": {
                if (registers[reg1Index].data < 0) {
                    pc = addressIndex;
                }
                break;
            }
        }
    }

    /**
     * An enum that depicts the current state of the CPU.
     */
    public enum CpuState {
        FREE,
        EXECUTING
    }

    @Override
    public String toString() {
        return "ID: " + cpuId + " | State: " + cpuState;
    }
}
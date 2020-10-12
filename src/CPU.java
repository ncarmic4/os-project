import java.math.BigInteger;

/**
 * Core Processing Unit.
 * A class to handle execution of instructions read from memory.
 */
public class CPU {
    // Determine opcode by indexing opcodeArray
    private final String[] opcodeArray = {"RD", "WR", "ST", "LW", "MOV", "ADD", "SUB", "MUL", "DIV", "AND",
            "OR", "MOVI", "ADDI", "MULI", "DIVI", "LDI", "SLT", "SLTI", "HLT", "NOP", "JMP", "BEQ",
            "BNE", "BEZ", "BNZ", "BGZ", "BLZ"};

    // Properties of binary instructions
    private String binary, address;
    private int reg1Index, reg2Index, reg3Index, addressIndex;

    MMU mmu;
    Registers[] registers;

    // Program continuation variables
    private int pc;
    private boolean continueExecution = true;

    public CPU (MMU mmu) {
        this.mmu = mmu;
    }

    public void setRegisters(Registers[] registers) {
        this.registers = registers;
    }

    /**
     * A wrapper method to load a variable at a specified address from RAM.
     * @param index Address of the variable.
     * @return Value of the variable.
     */
    String fetch(int index) {
        return mmu.loadRam(index);
    }

    /**
     * Decode a binary string of instructions.
     * Chars 0-1 indicate instruction format.
     * Chars 2-7 indicate instruction OPCODE.
     * Chars 8-32 indicate additional registers, addresses, etc.
     * @param hex Instruction string in hex format.
     */
    void decode(String hex) {
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
     * Main execution of the CPU.
     */
    void execute() {
        String hex = fetch(pc);
        pc++;
        decode(hex);

        // We set boolean continueExecution to false when the HLT opcode is called
        if(continueExecution) {
            execute();
        } else {
            // TODO: Figure out which register to output as final val
        }
    }

    /**
     * Method to initialize register indexes based on instruction format.
     * These are predefined, so for example arithmetic instructions specify reg1
     * in chars 8-11, reg2 in chars 12-15, and reg3 in chars 16-19.
     * @param format First two bits of binary instruction that specify type of instruction.
     */
    private void instructionFormat(String format) {
        switch (format) {
            case "00" -> { // ARITHMETIC
                String reg1 = binary.substring(8, 12);
                reg1Index = Integer.parseInt(reg1, 2);

                String reg2 = binary.substring(12, 16);
                reg2Index = Integer.parseInt(reg2, 2);

                String reg3 = binary.substring(16, 20);
                reg3Index = Integer.parseInt(reg3, 2);
            }
            case "01", "11" -> { // CONDITIONAL, INPUT/OUTPUT
                String reg1 = binary.substring(8, 12);
                reg1Index = Integer.parseInt(reg1, 2);

                String reg2 = binary.substring(12, 16);
                reg2Index = Integer.parseInt(reg2, 2);

                address = binary.substring(16, 32);
                addressIndex = Integer.parseInt(address, 2) / 4;
            }
            case "10" -> { // UNCONDITIONAL
                address = binary.substring(8, 32);
                addressIndex = Integer.parseInt(address, 2) / 4;
            }
        }
    }

    /**
     * Method to provide operations for each OPCODE.
     * @param opcode OPCODE string.
     */
    private void evaluate(String opcode) {
        switch (opcode) {
            case "RD" -> {
                if (addressIndex == 0) {
                    registers[reg1Index].data = Integer.parseInt(mmu.ram[registers[reg2Index].data], 16);
                } else {
                    registers[reg1Index].data = Integer.parseInt(mmu.ram[addressIndex], 16);
                }
            }
            case "WR" -> mmu.ram[addressIndex] = Integer.toHexString(registers[reg1Index].data);
            case "ST" -> {
                if (addressIndex == 0) {
                    mmu.ram[registers[reg2Index].data] = Integer.toHexString(registers[reg1Index].data);
                } else {
                    mmu.ram[addressIndex] = Integer.toHexString(registers[reg1Index].data);
                }
            }
            case "LW" -> {
                if (addressIndex == 0) {
                    registers[reg2Index].data = Integer.parseInt(mmu.ram[registers[reg1Index].data], 16);
                } else {
                    registers[reg2Index].data = Integer.parseInt(mmu.ram[addressIndex], 16);
                }
            }
            case "MOV" -> registers[reg3Index].data = registers[reg1Index].data;
            case "ADD" -> registers[reg3Index].data = registers[reg1Index].data + registers[reg2Index].data;
            case "SUB" -> registers[reg3Index].data = registers[reg1Index].data - registers[reg2Index].data;
            case "MUL" -> registers[reg3Index].data = registers[reg1Index].data * registers[reg2Index].data;
            case "DIV" -> {
                if (registers[reg2Index].data != 0) {
                    registers[reg3Index].data = registers[reg1Index].data / registers[reg2Index].data;
                }
            }
            case "AND" -> {
                if (registers[reg1Index].data != 0 && registers[reg2Index].data != 0) {
                    registers[reg3Index].data = 1;
                } else {
                    registers[reg3Index].data = 0;
                }
            }
            case "OR" -> {
                if (registers[reg1Index].data == 1 || registers[reg2Index].data == 1) {
                    registers[reg3Index].data = 1;
                } else {
                    registers[reg3Index].data = 0;
                }
            }
            case "MOVI" -> registers[reg2Index].data = Integer.parseInt(address, 2);
            case "ADDI" -> registers[reg2Index].data++;
            case "MULI" -> registers[reg2Index].data = registers[reg2Index].data * addressIndex;
            case "DIVI" -> {
                if (addressIndex != 0) {
                    registers[reg2Index].data = registers[reg2Index].data / addressIndex;
                }
            }
            case "LDI" -> registers[reg2Index].data = addressIndex;
            case "SLT" -> {
                if (registers[reg1Index].data < registers[reg2Index].data) {
                    registers[reg3Index].data = 1;
                } else {
                    registers[reg3Index].data = 0;
                }
            }
            case "SLTI" -> {
                if (registers[reg1Index].data < addressIndex) {
                    registers[reg2Index].data = 1;
                } else {
                    registers[reg2Index].data = 0;
                }
            }
            case "HLT" -> continueExecution = false;
            case "NOP" -> pc++;
            case "JMP" -> pc = addressIndex;
            case "BEQ" -> {
                if (registers[reg1Index].data == registers[reg2Index].data) {
                    pc = addressIndex;
                }
            }
            case "BNE" -> {
                if (registers[reg1Index].data != registers[reg2Index].data) {
                    pc = addressIndex;
                }
            }
            case "BEZ" -> {
                if (registers[reg2Index].data == 0) {
                    pc = addressIndex;
                }
            }
            case "BNZ" -> {
                if (registers[reg1Index].data != 0) {
                    pc = addressIndex;
                }
            }
            case "BGZ" -> {
                if (registers[reg1Index].data > 0) {
                    pc = addressIndex;
                }
            }
            case "BLZ" -> {
                if (registers[reg1Index].data < 0) {
                    pc = addressIndex;
                }
            }
        }
    }
}
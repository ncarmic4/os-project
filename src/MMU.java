import java.util.concurrent.Semaphore;

/**
 * Memory Management Unit.
 */
class MMU
{
    static String[] ram = new String[Driver.ramSize];
    static String[] disk = new String[Driver.diskSize];

    /**
     * Initialize Memory Management Unit with empty values.
     */
    static void init() {
        for (int i = 0; i < ram.length; i++) {
            storeRam(i, "");
        }
    }

    /**
     * Retrieve a section of memory that is empty.
     * @param size Size of the section of empty memory needed.
     * @return Starting index of the section.
     */
    static int nextAvailableBits(int size) {
        int numAvailable = 0;
        for (int i = 0; i < ram.length; i++) {
            if (numAvailable == size) {
                return i - size;
            }

            if(ram[i].isEmpty()) {
                numAvailable++;
            } else {
                numAvailable = 0;
            }
        }

        return -1;
    }

    /**
     * Clear a specific section of memory.
     * @param inclusiveStart Starting index of the section, inclusive.
     * @param exclusiveEnd Ending index of the section, exclusive.
     */
    static void clearBits(int inclusiveStart, int exclusiveEnd) {
        for (int i = inclusiveStart; i < exclusiveEnd; i++) {
            storeRam(i, "");
        }
    }

    /**
     * Load value at an address in memory.
     * @param address Index value of word.
     * @return Word at the index in memory.
     */
    static String loadRam(int address) {
        if (ram[address].isEmpty()) {
            return "";
        } else {
            return ram[address].substring(0, 8);
        }
    }

    /**
     * Store a value in memory.
     * @param address Location to store value in memory.
     * @param data Word to store in memory.
     */
    static void storeRam(int address, String data) {
        ram[address] = data;
    }

    /**
     * Load value at an address in disk.
     * @param address Index value of word.
     * @return Word at the index in disk.
     */
    static String loadDisk(int address) {
        return disk[address].substring(0, 8);
    }

    /**
     * Store a value in disk.
     * @param address Location to store value in disk.
     * @param data Word to store in disk.
     */
    static void storeDisk(int address, String data) {
        disk[address] = data;
    }

    /**
     * Get the current amount of words loaded in memory.
     * @return Amount of words.
     */
    static int getRamUsage() {
        int usage = 0;
        for (int i = 0; i < ram.length; i++) {
            if(!ram[i].equals("")) {
                usage++;
            }
        }
        return usage;
    }
}
import java.util.concurrent.Semaphore;

/**
 * Memory Management Unit.
 */
class MMU
{
    static String[] ram = new String[1024];
    static String[] disk = new String[2048];

    static void init() {
        for (int i = 0; i < ram.length; i++) {
            storeRam(i, "");
        }
    }

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

    static void clearBits(int inclusiveStart, int exclusiveEnd) {
        for (int i = inclusiveStart; i < exclusiveEnd; i++) {
            storeRam(i, "");
        }
    }

    static String loadRam(int address) {
        if (ram[address].isEmpty()) {
            return "";
        } else {
            return ram[address].substring(0, 8);
        }
    }

    static void storeRam(int address, String data) {
        ram[address] = data;
    }

    static String loadDisk(int address) {
        return disk[address].substring(0, 8);
    }

    static void storeDisk(int address, String data) {
        disk[address] = data;
    }
}
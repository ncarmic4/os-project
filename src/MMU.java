/**
 * Memory Management Unit.
 */
public class MMU
{
    public String[] ram = new String[1024];
    public String[] disk = new String[2048];

    public String loadRam(int address) {
        return ram[address].substring(0, 8);
    }

    public void storeRam(int address, String data) {
        ram[address] = data;
    }

    public String loadDisk(int address) {
        return disk[address].substring(0, 8);
    }

    public void storeDisk(int address, String data) {
        disk[address] = data;
    }
}
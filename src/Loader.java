import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Loader
{
    /**
     * Loads instructions from data file into disk array.
     * @param memMgr The memory manager which holds the disk array.
     */
    public static void load(MMU memMgr, Scheduler scheduler) {
        int index = 0;
        try {
            File f = new File("src/instructions.txt");
            Scanner scanner = new Scanner(f);
            PCB currentPcb = null;
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                if (line.startsWith("//")) {
                    String[] instrCodes = line.substring(3).split(" ");
                    if (instrCodes[0].equals("JOB")) {
                        currentPcb = new PCB(instrCodes[1], instrCodes[2], instrCodes[3], index);
                    } else if (instrCodes[0].equals("Data")) {
                        if (currentPcb != null) {
                            currentPcb.setInputBufferSize(Integer.parseInt(instrCodes[1], 16));
                            currentPcb.setOutputBufferSize(Integer.parseInt(instrCodes[2], 16));
                            currentPcb.setTempBufferSize(Integer.parseInt(instrCodes[3], 16));
                        }
                    } else if (instrCodes[0].equals("END")) {
                        scheduler.addJob(currentPcb);
                    }
                } else {
                    // Store instruction on disk
                    memMgr.storeDisk(index, line.substring(2, 10));
                    index++;
                }
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

class Loader
{
    /**
     * Loads instructions from data file into disk array.
     */
    static void load() {
        int index = 0;
        try {
            File f = new File("src/instructions.txt");
            Scanner scanner = new Scanner(f);
            PCB currentPcb = null;
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                if (line.startsWith("//")) {
                    String[] instrCodes = line.substring(3).split(" ");
                    switch (instrCodes[0]) {
                        case "JOB": {
                            currentPcb = new PCB(instrCodes[1], instrCodes[2], instrCodes[3], index);
                            break;
                        }
                        case "Data": {
                            if (currentPcb != null) {
                                currentPcb.setInputBufferSize(Integer.parseInt(instrCodes[1], 16));
                                currentPcb.setOutputBufferSize(Integer.parseInt(instrCodes[2], 16));
                                currentPcb.setTempBufferSize(Integer.parseInt(instrCodes[3], 16));
                            }
                            break;
                        }
                        case "END": {
                            if (currentPcb != null) {
                                currentPcb.setJobState(PCB.JobState.READY);
                                Scheduler.addJob(currentPcb);
                            }
                            break;
                        }
                    }
                } else {
                    // Store instruction on disk
                    MMU.storeDisk(index, line.substring(2, 10));
                    index++;
                }
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
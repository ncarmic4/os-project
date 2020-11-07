/**
 * Dispatches jobs to the CPU.
 */
class Dispatcher {

    /**
     * Assign a job to a CPU.
     * @param job PCB of a job.
     */
    static synchronized void loadJob(PCB job, CPU cpu) {
        int totalSize = job.getTotalSize();
        int diskStartIndex = job.getDiskStart();
        int ramStartIndex = accessRam(totalSize, diskStartIndex);
        int ramEndIndex = ramStartIndex + totalSize;

        job.setCurrentCpu(cpu);
        job.setRamStart(ramStartIndex);
        job.setRamEnd(ramEndIndex);

        // Load Instructions and data into RAM
        cpu.setRegisters(job.getRegisters());
        cpu.resetProgramCounter();
        cpu.setCurrentJob(job);
        job.setStartTime(System.currentTimeMillis());
        System.out.println(job);
    }

    static synchronized int accessRam(int totalSize, int diskStartIndex) {
        int ramStartIndex = MMU.nextAvailableBits(totalSize);
        for(int i = ramStartIndex; i < ramStartIndex + totalSize; i++) {
            MMU.storeRam(i, MMU.loadDisk(diskStartIndex + i - ramStartIndex));
        }
        return ramStartIndex;
    }

    static void unloadJob(PCB job, CPU cpu) {
        job.setCompletionTime(System.currentTimeMillis());
        job.setRegisters(cpu.getRegisters());
        cpu.setCurrentJob(null);
    }
}

/**
 * Dispatches jobs to the CPU.
 */
class Dispatcher {

    /**
     * Load a job onto a CPU.
     * @param job The job to be loaded.
     * @param cpu The CPU the job will be loaded to.
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

    /**
     * A synchronized method used to access RAM, the shared memory.
     * Synchronized methods only allow a single thread at a time to execute it. (Similar to Semaphore implementations).
     * @param totalSize Total number of instructions and buffer size of the job.
     * @param diskStartIndex The location in disk of the job's first instruction.
     * @return The start index in RAM of an empty section big enough to hold the job's full instruction and data set.
     */
    static synchronized int accessRam(int totalSize, int diskStartIndex) {
        int ramStartIndex = MMU.nextAvailableBits(totalSize);
        for(int i = ramStartIndex; i < ramStartIndex + totalSize; i++) {
            MMU.storeRam(i, MMU.loadDisk(diskStartIndex + i - ramStartIndex));
        }
        return ramStartIndex;
    }

    /**
     * Unload a job from a CPU.
     * @param job The job to be unloaded.
     * @param cpu The CPU the job will be unloaded from.
     */
    static void unloadJob(PCB job, CPU cpu) {
        job.setCompletionTime(System.currentTimeMillis());
        job.setRegisters(cpu.getRegisters());
        cpu.setCurrentJob(null);
    }
}

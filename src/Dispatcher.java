/**
 * Dispatches jobs to the CPU.
 */
class Dispatcher {

    /**
     * Assign a job to a CPU.
     * @param job PCB of a job.
     */
    static void load(PCB job, CPU cpu) {
        int totalSize = job.getNumInstructions() + job.getDataSize();
        int ramEndIndex = job.getRamStart() + totalSize;
        int diskStartIndex = job.getStart();
        int ramStartIndex = sync(totalSize, ramEndIndex, diskStartIndex);

        job.setCurrentCpu(cpu);
        job.setRamStart(ramStartIndex);
        job.setRamEnd(ramEndIndex);

        // Load Instructions and data into RAM
        cpu.setRegisters(job.getRegisters());
        cpu.setCpuState("executing");
        cpu.setProgramCounter(diskStartIndex + job.getProgramCounter());
        cpu.setCurrentJob(job);
    }

    static synchronized int sync(int totalSize, int ramEndIndex, int diskStartIndex) {
        int ramStartIndex = MMU.nextAvailableBits(totalSize); //
        for(int i = ramStartIndex; i < ramEndIndex; i++) { //
            MMU.storeRam(i, MMU.loadDisk(diskStartIndex + i - ramStartIndex));
        }
        return ramStartIndex;
    }

    static void unload(PCB job, CPU cpu) {
        job.setCompletedTime(System.currentTimeMillis());
        MetricCollector.addJob(job);
        job.setRegisters(cpu.getRegisters());
        cpu.setCurrentJob(null);
        job.setCurrentCpu(null);
    }
}

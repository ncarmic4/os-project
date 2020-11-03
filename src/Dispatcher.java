/**
 * Dispatches jobs to the CPU.
 */
public class Dispatcher {
    // TODO: Implement dispatcher

    MMU mmu;

    public Dispatcher(MMU mmu) {
        this.mmu = mmu;
    }

    /**
     * Assign a job to a CPU.
     * @param pcb PCB of a job.
     * @param cpu CPU to assign the job to.
     */
    public void dispatch(PCB pcb, CPU cpu) {
        for(int i = 0; i < pcb.numInstructions; i++) {
            mmu.storeRam(i, mmu.loadDisk(pcb.getFirstInstruction() + i));
            System.out.println(mmu.loadDisk(pcb.getFirstInstruction() + i) + "=" + mmu.loadRam(i));
        }
        cpu.setProgramCounter(pcb.getFirstInstruction());
    }
}

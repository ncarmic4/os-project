/**
 * Main OS Execution Driver.
 */
public class Driver {
    public static void main(String[] args) {
        // Initialize components
        MMU mmu = new MMU();
        CPU cpu = new CPU(mmu);
        Scheduler scheduler = new Scheduler();
        Dispatcher dispatcher = new Dispatcher(mmu);

        // Program execution
        Loader.load(mmu, scheduler);
        scheduler.sortList();
        while (!scheduler.allJobsDone()) {
            PCB nextJob = scheduler.nextJob();
            dispatcher.dispatch(nextJob, cpu);
            cpu.execute();
            scheduler.waitForInterrupt();
        }
    }
}

/**
 * Main OS Execution Driver.
 */
public class Driver {
    public static void main(String[] args) throws InterruptedException {
        Loader.load();
        MMU.init();

        for (int i = 0; i < 4; i++) {
            CPU cpu = new CPU(i);
            cpu.setName("CPU " + i);
            cpu.start();
            cpu.join();
        }

        MetricCollector.listTimes();
    }
}

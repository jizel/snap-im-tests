package travel.snapshot.qa.manager.api.io;

public class ContainerShutdownThread extends Thread {

    private final Process containerProcess;

    public ContainerShutdownThread(Process containerProcess) {
        this.containerProcess = containerProcess;
    }

    @Override
    public void run() {
        if (containerProcess != null) {
            containerProcess.destroy();
            try {
                containerProcess.waitFor();
            } catch (final InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

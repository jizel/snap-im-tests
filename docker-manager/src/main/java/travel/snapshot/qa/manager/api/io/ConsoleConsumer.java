package travel.snapshot.qa.manager.api.io;

import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ConsoleConsumer implements Runnable {

    private final Process process;

    private final Logger logger;

    private boolean outputToConsole = true;

    public ConsoleConsumer(final Process process, Logger logger) {
        this.process = process;
        this.logger = logger;
    }

    public ConsoleConsumer setOutputToConsole(boolean outputToConsole) {
        this.outputToConsole = outputToConsole;
        return this;
    }

    public boolean isOutputToConsole() {
        return outputToConsole;
    }

    @Override
    public void run() {
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (isOutputToConsole()) {
                    logger.info(line);
                }
            }
        } catch (IOException e) {
            // intentionally empty
        }
    }
}

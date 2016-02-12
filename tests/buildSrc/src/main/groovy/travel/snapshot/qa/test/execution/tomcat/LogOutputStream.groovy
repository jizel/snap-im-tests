package travel.snapshot.qa.test.execution.tomcat

import org.slf4j.Logger

public class LogOutputStream extends OutputStream {

    private static final String EMPTY_STRING = ""

    private Logger logger

    private String mem

    /**
     * Creates a new log output stream which logs bytes to the specified logger with the specified
     * level.
     *
     * @param logger the logger where to log the written bytes
     * @param level the level
     */
    LogOutputStream(Logger logger) {
        setLogger(logger);
        mem = EMPTY_STRING;
    }

    /**
     * Sets the logger where to log the bytes.
     *
     * @param logger the logger
     */
    LogOutputStream setLogger(Logger logger) {
        this.logger = logger
        this
    }

    Logger getLogger() {
        logger
    }

    @Override
    void write(int b) {
        byte[] bytes = new byte[1]
        bytes[0] = (byte) (b & 0xff)
        mem = mem + new String(bytes)

        if (mem.endsWith("\n")) {
            mem = mem.substring(0, mem.length() - 1)
            flush()
        }
    }

    @Override
    public void flush() {
        logger.info(mem)
        mem = EMPTY_STRING
    }
}

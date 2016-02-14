package travel.snapshot.qa.util

class Timer {

    def start

    def stop

    def start() {
        start = System.currentTimeMillis()
    }

    def stop() {
        stop = System.currentTimeMillis()
    }

    /**
     * Cleans start and stop counters, delta method will return 0 after this is called.
     */
    def reset() {
        start = 0
        stop = 0
    }

    /**
     *
     * @return time in seconds between invocation of start and stop methods
     */
    def delta() {
        (stop - start) / 1000
    }
}

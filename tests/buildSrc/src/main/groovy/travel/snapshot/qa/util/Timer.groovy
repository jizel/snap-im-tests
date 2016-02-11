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

    def reset() {
        start = 0
        stop = 0
    }

    def delta() {
        (stop - start) / 1000
    }
}

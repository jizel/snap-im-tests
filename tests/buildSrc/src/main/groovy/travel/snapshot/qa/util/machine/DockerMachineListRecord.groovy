package travel.snapshot.qa.util.machine

class DockerMachineListRecord {

    def name

    def active

    def driver

    def state

    def url

    static List<DockerMachineListRecord> parse(List<String> lines) {

        // skip header
        if (lines.size() > 0) {
            lines.remove(0)
        }

        List<DockerMachineListRecord> records = []

        lines.each { line ->
            String[] splits = line.trim().replaceAll(" +", " ").split(" ")

            DockerMachineListRecord record = new DockerMachineListRecord()

            record.name = splits[0]
            record.active = splits[1]
            record.driver = splits[2]
            record.state = splits[3]
            record.url = splits[4]

            records << record
        }

        records
    }

    @Override
    String toString() {
        String.format("%s %s %s %s %s", name, active, driver, state, url)
    }
}

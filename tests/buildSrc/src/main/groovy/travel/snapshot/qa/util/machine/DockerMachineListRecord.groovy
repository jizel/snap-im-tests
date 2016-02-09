package travel.snapshot.qa.util.machine

/**
 * Abstraction of Docker machine. The output line of 'docker-machine ls' represents this class.
 */
class DockerMachineListRecord {

    String name

    String active

    String driver

    String state

    String url

    /**
     * Parses Docker machines from its raws output from 'docker-machine ls'
     *
     * @param lines raw lines from 'docker-machine ls' output
     * @return parsed Docker images
     */
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

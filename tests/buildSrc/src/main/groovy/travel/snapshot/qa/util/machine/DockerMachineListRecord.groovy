package travel.snapshot.qa.util.machine

/**
 * Abstraction of Docker machine. The output line of 'docker-machine ls' represents this class.
 */
class DockerMachineListRecord {

    String name

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

        List<DockerMachineListRecord> records = []

        lines.each { line ->
            String[] splits = line.tokenize(":")

            DockerMachineListRecord record = new DockerMachineListRecord()

            record.name = splits[0]
            record.driver = splits[1]
            record.state = splits[2]

            if (splits.length == 4) {
                record.url = splits[3]
            }

            records << record
        }

        records
    }

    @Override
    String toString() {
        String.format("%s %s %s %s", name, driver, state, url)
    }
}

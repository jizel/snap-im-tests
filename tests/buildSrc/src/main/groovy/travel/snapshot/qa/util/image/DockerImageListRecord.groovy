package travel.snapshot.qa.util.image

class DockerImageListRecord {

    String repository

    String tag

    String imageId

    static List<DockerImageListRecord> parse(List<String> lines) {

        // skip header
        if (lines.size() > 0) {
            lines.remove(0)
        }

        List<DockerImageListRecord> images = []

        lines.each { line ->
            String[] splits = line.trim().replaceAll(" +", " ").split(" ")

            DockerImageListRecord image = new DockerImageListRecord()
            image.repository = splits[0]
            image.tag = splits[1]
            image.imageId = splits[2]

            images << image
        }

        images
    }

    @Override
    String toString() {
        String.format("%s %s %s", repository, tag, imageId)
    }
}

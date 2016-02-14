package travel.snapshot.qa.util.image

import org.arquillian.spacelift.Spacelift
import org.arquillian.spacelift.process.ProcessResult

class DockerImage {

    /**
     * Parses images to model classes - executes 'docker images' command internally.
     *
     * @return locally installed images
     */
    static List<DockerImageListRecord> parseImages() {
        ProcessResult result = Spacelift.task("docker").parameter("images").execute().await()
        DockerImageListRecord.parse(result.output())
    }

    /**
     * Installs images from registry - executes 'docker pull' command internally
     *
     * @param images images to download
     */
    static def downloadImages(List<String> images) {
        for (String image : images) {
            Spacelift.task("docker").parameters("pull", image).execute().await()
        }
    }

    /**
     * Filters images which are missing locally
     *
     * @param images images to filter
     * @return images which are missing locally
     */
    static List<String> getMissingImages(List<String> images) {
        List<String> missingImages = []
        List<DockerImageListRecord> downloadedImages = parseImages()

        for (String image : images) {

            boolean isMissing = true

            for (DockerImageListRecord downloadedImage : downloadedImages) {

                String imageRepository = DockerImage.getRepository(image)
                String imageTag = DockerImage.getTag(image)

                if (imageRepository == downloadedImage.repository && imageTag == downloadedImage.tag) {
                    isMissing = false
                    break
                }
            }

            if (isMissing) {
                missingImages << image
            }
        }
        
        missingImages
    }

    /**
     * Gets repository part of some image
     *
     * @param image gets repository from image
     * @return repository of image
     */
    static String getRepository(String image) {
        valueOf(image).get(0)
    }

    /**
     * Gets tag part of some image
     *
     * @param image gets tag from image
     * @return tag of image
     */
    static String getTag(String image) {
        valueOf(image).get(1)
    }

    static def valueOf(String image) {
        def name
        def tag

        // <repositoryurl>:<port>/<organization_namespace>/<image_name>:<tag>
        String[] parts = image.split("/")

        switch (parts.length) {
            case 1: // <image_name>[:<tag>]
            case 2: // <organization_namespace>/<image_name>[:tag]
                String imageName = image
                final int colonIndex = imageName.indexOf(':')
                if (colonIndex > -1) {
                    name = imageName.substring(0, colonIndex)
                    tag = imageName.substring(colonIndex + 1)
                } else {
                    name = imageName
                }
                break
            case 3:  // <repositoryurl>[:<port>]/<organization_namespace>/<image_name>[:<tag>]
                String imageName = parts[2]
                final int colonIndex = imageName.indexOf(':')
                if (colonIndex > -1) {
                    name = parts[0] + "/" + parts[1] + "/" + imageName.substring(0, colonIndex)
                    tag = imageName.substring(colonIndex + 1)
                } else {
                    name = image
                }
        }

        [name, tag]
    }
}

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
                if (image.startsWith(downloadedImage.repository) && image.endsWith(downloadedImage.tag)) {
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
}

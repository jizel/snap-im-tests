package travel.snapshot.qa.util.image

import org.arquillian.spacelift.Spacelift
import org.arquillian.spacelift.process.ProcessResult

class DockerImage {

    static List<DockerImageListRecord> parseImages() {
        ProcessResult result = Spacelift.task("docker").parameter("images").execute().await()
        DockerImageListRecord.parse(result.output())
    }

    static def downloadImages(List images) {
        for (String image : images) {
            Spacelift.task("docker").parameters("pull", image).execute().await()
        }
    }

    static List<String> getMissingImages(List images) {
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

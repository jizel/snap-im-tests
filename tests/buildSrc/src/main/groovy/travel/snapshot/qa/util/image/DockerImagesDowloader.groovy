package travel.snapshot.qa.util.image

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import travel.snapshot.qa.util.auth.DockerAuth

/**
 * Downloads Docker images
 */
class DockerImagesDowloader {

    private static final Logger logger = LoggerFactory.getLogger(DockerImagesDowloader)

    /**
     * Downloads Docker images from Docker repository.
     *
     * @param images images to download
     * @param registryPassword password for Docker images repository
     */
    static def download(List<String> images, String registryPassword) {

        if (!images.isEmpty()) {
            List<String> missingImages = DockerImage.getMissingImages(images)

            if (!missingImages.isEmpty()) {
                logger.info("Going to download images missing images {}.", missingImages)

                DockerAuth.loginToDockerRegistry(registryPassword, "snapshot_docker", "docker@snapshot.travel", "docker.snapshot.travel:5000")
                DockerImage.downloadImages(missingImages)
            }
        }
    }
}

package travel.snapshot.qa.util.image

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import travel.snapshot.qa.util.auth.DockerAuth

class DockerImagesDowloader {

    private static final Logger logger = LoggerFactory.getLogger(DockerImagesDowloader)

    static def download(List images, String registryPassword) {

        if (!images.isEmpty()) {
            List missingImages = DockerImage.getMissingImages(images)

            logger.info("Going to download images missing images {}.", missingImages)

            if (!missingImages.isEmpty()) {
                DockerAuth.loginToDockerRegistry(registryPassword, "snapshot_docker", "docker@snapshot.travel", "docker.snapshot.travel:5000")
                DockerImage.downloadImages(missingImages)
            }
        }
    }
}

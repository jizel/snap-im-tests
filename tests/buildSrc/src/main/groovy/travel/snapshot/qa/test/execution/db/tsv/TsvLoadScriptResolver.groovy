package travel.snapshot.qa.test.execution.db.tsv

import org.apache.commons.lang3.SystemUtils
import org.arquillian.spacelift.Spacelift
import org.arquillian.spacelift.gradle.text.ProcessTemplate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import travel.snapshot.qa.util.Properties

class TsvLoadScriptResolver {

    private static final Logger logger = LoggerFactory.getLogger(TsvLoadScriptResolver)

    static def resolve() {

        String qaRepositoryLocation = Properties.Location.dataPlatformQARepository.absolutePath

        String qaRepositoryTemplate = getPlatformSpecificDir(qaRepositoryLocation)

        File tsvTemplate = new File(qaRepositoryLocation, "fake-tsv-data/template_load.sql")

        logger.info("Going to expand TSV template file {} with {}.", tsvTemplate.absolutePath, qaRepositoryTemplate)

        File output = Spacelift.task(tsvTemplate, ProcessTemplate)
                .bindings(["path": qaRepositoryTemplate])
                .execute()
                .await()

        logger.debug("Going to use this load.sql file for TSV import.")
        logger.debug(output.text)

        output.absolutePath
    }

    private static String getPlatformSpecificDir(String dir) {

        if (SystemUtils.IS_OS_UNIX) {
            return dir;
        } else if (SystemUtils.IS_OS_WINDOWS) {
            return dir.replaceAll("\\\\", "/")
        } else {
            throw new IllegalStateException("Unsupported platform.")
        }
    }
}

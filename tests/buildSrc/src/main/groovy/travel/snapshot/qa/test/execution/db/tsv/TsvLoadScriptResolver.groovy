package travel.snapshot.qa.test.execution.db.tsv

import org.arquillian.spacelift.Spacelift
import org.arquillian.spacelift.gradle.text.ProcessTemplate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import travel.snapshot.qa.util.PropertyResolver

class TsvLoadScriptResolver {

    private static final Logger logger = LoggerFactory.getLogger(TsvLoadScriptResolver)

    static def resolve() {

        File qaRepository = PropertyResolver.resolveDataPlatformQARepositoryLocation()

        File tsvTemplate = new File(qaRepository, "fake-tsv-data/template_load.sql")

        File output = Spacelift.task(tsvTemplate, ProcessTemplate)
                .bindings(["path": qaRepository.absolutePath])
                .execute().await()

        logger.debug("Going to use this load.sql file for TSV import.")
        logger.debug(output.text)

        output.absolutePath
    }
}

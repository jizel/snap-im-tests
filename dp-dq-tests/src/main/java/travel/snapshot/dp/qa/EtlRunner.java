package travel.snapshot.dp.qa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import travel.snapshot.dp.etl.Main;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.Validate.notEmpty;
import static travel.snapshot.dp.qa.ConfigProps.getPropValue;

/**
 * Wrapper for running ETL process.
 *
 * Typical usage is via {@link #runEtl()} method.
 * You can also try manual run via {@link #main(String[])} method.
 *
 * Note that you need to set ensure that proper configuration for ETL tool is in place.
 * Check ETL tool's application.conf and override required properties via system properties.
 *
 * Typically, this can involve all username and password properties like:
 * - dwh.username
 * - dwh.password
 * - metadata.username
 * - metadata.password
 * You should pass all these properties (and others if applicable) to the application which invokes
 * this class.
 */
public class EtlRunner {

    private static final Logger logger = LoggerFactory.getLogger(EtlRunner.class);

    /**
     * Executes etl process using default arguments values.
     * Most important is the default "startdate" as specified by "etl.startDate" property.
     *
     * Note, that you can also set "etl.args" configuration property to provide completely
     * customized command line arguments for etl tool.
     *
     * @see ConfigProps
     */
    public static void runEtl() {
        final String etlArgs = getPropValue("etl.args");
        if (isNotBlank(etlArgs)) {
            runEtl(splitEtlArgs(etlArgs));
        } else {
            runEtl(new String[] {"--startdate", getPropValue("etl.startdate")});
        }

    }

    /**
     * Executes etl process with given command line arguments
     * @param args command line arguments for ETL tool
     */
    public static void runEtl(String[] args) {
        logger.info("action=run_etl_tool status=START args={}", Arrays.toString(args));
        initSystemProperties();
        try {
            Main.main(args);
            logger.info("action=run_etl_tool status=FINISH");
        } catch(Exception e) {
            logger.info("action=run_etl_tool status=ERROR errorMessage={}", e.getMessage());
        }
    }

    /**
     * For manual testing - you can invoke ETL tool via this method
     * @param args ignored - use "etl.args" configuration property if you cant completely
     *             customized arguments for ETL tool, or specific "etl." configuration properties
     */
    public static void main(String[] args) {
        runEtl();
    }


    /**
     * Splits etl args which are in single string into multiple arguments because this is a required
     * format when passing them to the main method.
     */
    private static String[] splitEtlArgs(String etlArgs) {
        notEmpty(etlArgs, "etlArgs cannot be empty!");
        return etlArgs.split(" ");
    }

    /**
     * In order to configure ETL tool properly we need to propagate all required settings from
     * our configuration into the ETL tool via System properties.
     */
    private static void initSystemProperties() {
        initProperty("dma.driverClass");
        initProperty("dma.connectionString");
        initProperty("dma.username");
        initProperty("dma.password");
        initProperty("dma.db");

        initProperty("dwh.driverClass");
        initProperty("dwh.connectionString");
        initProperty("dwh.username");
        initProperty("dwh.password");
    }


    private static String initProperty(String property) {
        return System.setProperty(property, getPropValue(property));
    }

}

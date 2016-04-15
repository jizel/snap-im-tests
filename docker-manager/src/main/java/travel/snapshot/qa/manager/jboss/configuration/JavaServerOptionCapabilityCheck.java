package travel.snapshot.qa.manager.jboss.configuration;

import org.apache.commons.lang3.SystemUtils;
import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.process.CommandBuilder;
import org.arquillian.spacelift.task.Task;
import org.arquillian.spacelift.task.os.CommandTool;

import java.util.List;

public class JavaServerOptionCapabilityCheck extends Task<JVM, Boolean> {

    private static final String[] JAVA_VENDORS = {
            "HotSpot",
            "OpenJDK",
            "IBM J9"
    };

    @Override
    protected Boolean process(JVM jvm) throws Exception {

        List<String> javaVersionOutput = Spacelift.task(CommandTool.class)
                .command(new CommandBuilder(jvm.getJavaBin()).build())
                .parameter("-version")
                .execute()
                .await()
                .output();

        boolean isServerCapable = false;

        for (String javaVendor : JAVA_VENDORS) {
            isServerCapable = checkServerOptionCapability(javaVendor, javaVersionOutput);
            if (isServerCapable) {
                break;
            }
        }

        // according to domain.sh script, -server option is not supported for MacOS
        return isServerCapable && !SystemUtils.IS_OS_MAC;
    }

    private boolean checkServerOptionCapability(String javaVendor, List<String> javaVersionOutput) {

        for (String line : javaVersionOutput) {
            if (line != null && line.length() != 0) {
                if (line.toLowerCase().contains(javaVendor.toLowerCase())) {
                    return true;
                }
            }
        }

        return false;
    }

}

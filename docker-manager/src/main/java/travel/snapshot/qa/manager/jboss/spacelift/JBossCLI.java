package travel.snapshot.qa.manager.jboss.spacelift;

import org.apache.commons.lang3.SystemUtils;
import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.process.CommandBuilder;
import org.arquillian.spacelift.process.ProcessResult;
import org.arquillian.spacelift.task.Task;
import org.arquillian.spacelift.task.os.CommandTool;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JBossCLI extends Task<Object, ProcessResult> {

    private final Map<String, String> environment = new HashMap<>();

    private String user;

    private String password;

    private File file;

    private String controller;

    private boolean connect = false;

    private List<String> command = new ArrayList<>();

    private List<Integer> exitCodes = new ArrayList<>();

    public JBossCLI environment(String key, String value) {
        if (key != null && value != null) {
            environment.put(key, value);
        }
        return this;
    }

    public JBossCLI environment(Map<String, String> environment) {
        if (environment != null) {
            environment.entrySet().stream().filter(entry -> entry != null).forEach(entry -> environment(entry.getKey(), entry.getValue()));
        }
        return this;
    }

    public JBossCLI user(String user) {
        this.user = user;
        return this;
    }

    public JBossCLI password(String password) {
        this.password = password;
        return this;
    }

    public JBossCLI file(String file) {
        this.file = new File(file);
        return this;
    }

    public JBossCLI file(File file) {
        this.file = file;
        return this;
    }

    public JBossCLI controller(String controller) {
        this.controller = controller;
        return this;
    }

    public JBossCLI connect() {
        this.connect = true;
        return this;
    }

    public JBossCLI cliCommand(String... command) {
        this.command.addAll(Arrays.asList(command));
        return this;
    }

    public JBossCLI shouldExitWith(Integer... exitCodes) {
        this.exitCodes.addAll(Arrays.asList(exitCodes));
        return this;
    }

    @Override
    protected ProcessResult process(Object input) throws Exception {

        final CommandTool jbossCliTool = getJBossCliTool();

        if (controller != null) {
            jbossCliTool.parameter("--controller=" + controller);
        }

        if (connect) {
            jbossCliTool.parameter("--connect");
        }

        if (file != null) {
            jbossCliTool.parameter("--file=" + file.getAbsolutePath());
        }

        if (this.command.size() == 1) {
            jbossCliTool.parameter("--command=" + this.command.get(0));
        }

        if (this.command.size() > 1) {
            jbossCliTool.parameter("--commands=" + getCommands());
        }

        if (user != null) {
            jbossCliTool.parameter("--user=" + user);
        }

        if (password != null) {
            jbossCliTool.parameter("--password=" + password);
        }

        jbossCliTool.shouldExitWith(exitCodes.toArray(new Integer[exitCodes.size()]));

        environment.putIfAbsent("JBOSS_HOME", JBossManagerConfiguration.Util.getJBossHome());

        return jbossCliTool.execute().await();
    }

    private String getCommands() {
        StringBuilder sb = new StringBuilder();
        String delim = "";

        for (String command : this.command) {
            sb.append(delim).append(command);
            delim = ",";
        }

        return sb.toString();
    }

    private CommandTool getJBossCliTool() throws Exception {

        if (SystemUtils.IS_OS_WINDOWS) {

            File jbossScript = new File(environment.get("JBOSS_HOME"), "/bin/jboss-cli.bat");

            validateScript(jbossScript);

            return Spacelift.task(CommandTool.class)
                    .command(new CommandBuilder("cmd.exe"))
                    .parameters("/C", jbossScript.getAbsolutePath())
                    .addEnvironment(environment);
        } else {

            File jbossScript = new File(environment.get("JBOSS_HOME"), "/bin/jboss-cli.sh");

            validateScript(jbossScript);

            return Spacelift.task(CommandTool.class)
                    .command(new CommandBuilder(jbossScript.getAbsolutePath()))
                    .addEnvironment(environment);
        }
    }

    private void validateScript(File jbossScript) throws Exception {
        if (!jbossScript.exists()) {
            throw new FileNotFoundException(jbossScript.getAbsolutePath() + " does not exist.");
        }

        if (!jbossScript.canExecute()) {
            throw new NotExecutableScriptException(jbossScript + " is not executable.");
        }
    }

    public static class NotExecutableScriptException extends Exception {

        private static final long serialVersionUID = -2281993207167380102L;

        public NotExecutableScriptException() {
            super();
        }

        public NotExecutableScriptException(String message, Throwable cause) {
            super(message, cause);
        }

        public NotExecutableScriptException(String message) {
            super(message);
        }

        public NotExecutableScriptException(Throwable cause) {
            super(cause);
        }

    }

}

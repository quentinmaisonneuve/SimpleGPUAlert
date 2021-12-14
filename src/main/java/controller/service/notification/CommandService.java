package controller.service.notification;

import controller.Daemon;
import controller.service.PropertyManager;
import model.GPUInfo;
import org.apache.commons.io.IOUtils;
import org.apache.maven.shared.utils.StringUtils;

import java.io.File;
import java.io.IOException;

public class CommandService implements NotificationService {

    // Constants
    public static final String COMMAND = "COMMAND";
    public static final String SHELL = "SHELL";

    @Override
    public void sendNotification(GPUInfo gpuInfo) {

        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        String shell;
        String homeDirectory;
        String commandToExecute;
        Process process;

        if (StringUtils.isNotBlank(PropertyManager.getProperty(SHELL))) {

            shell = PropertyManager.getProperty(SHELL);

        } else {

            if (isWindows) {

                shell = "cmd.exe";

            } else {

                shell = "sh";
            }
        }

        try {

            commandToExecute = String.format("%s /c %s",
                    shell,
                    PropertyManager.getProperty(COMMAND));

             process = Runtime.getRuntime().exec(commandToExecute);

            Daemon.logger.info("Command : ".concat(commandToExecute));
            Daemon.logger.info("Command result :");
            Daemon.logger.info(new String(IOUtils.toByteArray(process.getInputStream())));

        } catch (Exception e) {

            Daemon.logger.error(e);
        }
    }
}

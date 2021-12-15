package controller.service.notification;

import controller.Daemon;
import controller.service.PropertyManager;
import model.GPUInfo;
import org.apache.commons.io.IOUtils;
import org.apache.maven.shared.utils.StringUtils;

import java.io.File;

public class CommandService implements NotificationService {

    // Constants
    public static final String COMMAND = "COMMAND";
    public static final String ENV_VAR = "ENV_VAR";
    public static final String DIRECTORY = "DIRECTORY";

    @Override
    public void sendNotification(GPUInfo gpuInfo) {

        String command;
        String[] envVar = null;
        String directory;
        Process process;

        if (StringUtils.isNotBlank(PropertyManager.getProperty(ENV_VAR))) {

            envVar = PropertyManager.getProperty(ENV_VAR).split(",");
        }

        if (StringUtils.isNotBlank(PropertyManager.getProperty(DIRECTORY))) {

            directory = PropertyManager.getProperty(DIRECTORY);

        } else {

            directory = System.getProperty("user.home");
        }

        try {

            command = NotificationManager.formatString(PropertyManager.getProperty(COMMAND), gpuInfo, false);

            process = Runtime.getRuntime().exec(command,
                    envVar,
                    new File(directory));

            Daemon.logger.info("Command : ".concat(command));
            Daemon.logger.info("Environment variable(s) : ".concat(PropertyManager.getProperty(ENV_VAR)));
            Daemon.logger.info("Directory : ".concat(directory));
            Daemon.logger.info("Command result :");
            Daemon.logger.info(new String(IOUtils.toByteArray(process.getInputStream())));

        } catch (Exception e) {

            Daemon.logger.error(e);
        }
    }
}

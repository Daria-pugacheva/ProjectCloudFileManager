package ru.gb.pugacheva.server.service.impl.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.gb.pugacheva.common.domain.Command;
import ru.gb.pugacheva.common.domain.CommandType;
import ru.gb.pugacheva.server.factory.Factory;
import ru.gb.pugacheva.server.service.AuthenticationService;
import ru.gb.pugacheva.server.service.CommandService;
import ru.gb.pugacheva.server.service.impl.ServerPropertiesReciever;

import java.io.File;

public class AuthenticationCommand implements CommandService {

    private final AuthenticationService authenticationService;
    private static final Logger LOGGER = LogManager.getLogger(AuthenticationCommand.class);

    public AuthenticationCommand() {
        this.authenticationService = Factory.getAuthenticationService();
    }

    @Override
    public String processCommand(Command command) {
        final int requirementCountCommandArgs = 2;
        if (command.getArgs().length != requirementCountCommandArgs) {
            LOGGER.error("Command " + getCommand() + " is not correct");
            throw new IllegalArgumentException("Command " + getCommand() + " is not correct");
        }
        return process((String) command.getArgs()[0], (String) command.getArgs()[1]);
    }

    private String process(String login, String password) {
        if (authenticationService.isClientRegistered(login, password)) {
            return CommandType.LOGIN_OK + " " + login;

        } else if (authenticationService.registerClient(login, password)) {
            createUserDirectoryInCloud(login);
            return CommandType.LOGIN_OK + " " + login;
        }

        return CommandType.REGISTRATION_FAILED + " " + login;
    }

    private void createUserDirectoryInCloud(String login) {
        String pathToDir = String.format(ServerPropertiesReciever.getCloudDirectory() + "/%s/", login);
        File file = new File(pathToDir);
        file.mkdir();
    }

    @Override
    public String getCommand() {
        return CommandType.LOGIN.toString();
    }
}

package ru.gb.pugacheva.server.service.impl.command;

import ru.gb.pugacheva.common.domain.Command;
import ru.gb.pugacheva.common.domain.PropertiesReciever;
import ru.gb.pugacheva.server.factory.Factory;
import ru.gb.pugacheva.server.service.AuthenticationService;
import ru.gb.pugacheva.server.service.CommandService;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AuthenticationCommand implements CommandService {

    private AuthenticationService authenticationService;

    public AuthenticationCommand(){
        this.authenticationService = Factory.getAuthenticationService();
    }

    @Override
    public String processCommand(Command command) {
        final int requirementCountCommandArgs = 2;
        if (command.getArgs().length != requirementCountCommandArgs) {
            throw new IllegalArgumentException("Command " + getCommand() + " is not correct");
        }
        return process((String) command.getArgs()[0], (String) command.getArgs()[1]);
    }


    private String process(String login, String password) {
        if (authenticationService.isClientRegistered(login, password)) {
            return "loginOK " + login;
        } else if (authenticationService.registerClient(login, password)) {
            createUserDirectoryInCloud(login);
            return "loginOK " + login;
        }
        return "registrationFailed " + login;
    }

    private void createUserDirectoryInCloud (String login){
        String pathToDir = String.format(PropertiesReciever.getProperties("cloudDirectory") + "/%s/", login);
        File file = new File(pathToDir);
        file.mkdir();
    }

    @Override
    public String getCommand() {
        return "login";
    }
}

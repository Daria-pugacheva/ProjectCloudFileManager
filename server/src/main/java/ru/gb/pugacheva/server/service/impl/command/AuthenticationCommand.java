package ru.gb.pugacheva.server.service.impl.command;

import ru.gb.pugacheva.common.domain.Command;
import ru.gb.pugacheva.server.factory.Factory;
import ru.gb.pugacheva.server.service.AuthenticationService;
import ru.gb.pugacheva.server.service.CommandService;

import java.io.File;

public class AuthenticationCommand implements CommandService <String> {

    private AuthenticationService authenticationService;

    @Override
    public String processCommand(Command command) {
        final int requirementCountCommandArgs = 2;
        //String [] actualCommandParts = command.split("\\s");
        //if(actualCommandParts.length !=requirementCountCommandParts){
            if(command.getArgs().length != requirementCountCommandArgs){
            throw new IllegalArgumentException("Command " + getCommand() + " is not correct");
        }
        //return process(actualCommandParts[1],actualCommandParts[2]);
        return process((String) command.getArgs()[0],(String) command.getArgs()[1]);
    }


    private String process (String login, String password ){
        authenticationService = Factory.getAuthenticationService();
        authenticationService.open(); // TODO: надо еще закрыть его
        if(authenticationService.isClientRegistered(login, password)){
            return "loginOK " + login;
        }else if (authenticationService.registerClient(login, password)){
            String pathToDir = String.format("C:/java/Course_Project_Cloud/my-cloud-project/Cloud/%s/",login);
            File file = new File(pathToDir);
            file.mkdir();
            return "loginOK " + login; // был вариант с registrationOK, но я упростила
        }
        authenticationService.close();
        return "registrationFailed " + login;
    }

    @Override
    public String getCommand() {
        return "login";
    }
}

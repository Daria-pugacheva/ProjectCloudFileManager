package ru.gb.pugacheva.server.service.impl.command;

import ru.gb.pugacheva.server.factory.Factory;
import ru.gb.pugacheva.server.service.AuthenticationService;
import ru.gb.pugacheva.server.service.CommandService;

import java.io.File;

public class AuthenticationCommand implements CommandService {

    private AuthenticationService authenticationService;

    @Override
    public String processCommand(String command) {
        final int requirementCountCommandParts = 3;
        String [] actualCommandParts = command.split("\\s");
        if(actualCommandParts.length !=requirementCountCommandParts){
            throw new IllegalArgumentException("Command " + getCommand() + "is not correct");

        }
        return process(actualCommandParts[1],actualCommandParts[2]);
    }


    private String process (String login, String password ){
        authenticationService = Factory.getAuthenticationService();
        authenticationService.open();
        if(authenticationService.isClientRegistered(login, password)){
            return "loginOK"; //тут все срабатывает
        }else if (authenticationService.registerClient(login, password)){
            File file = new File(String.format("C:/java/Course_Project_Cloud/my-cloud-project/Cloud/%s",login));
            file.mkdir();
            return "registrationOK"; //TODO: проверить метод регистрации
        }
        return "registrationFailed";

    }

    @Override
    public String getCommand() {
        return "login";
    }
}

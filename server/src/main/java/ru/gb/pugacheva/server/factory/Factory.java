package ru.gb.pugacheva.server.factory;

import ru.gb.pugacheva.server.service.*;
import ru.gb.pugacheva.server.service.impl.*;
import ru.gb.pugacheva.server.service.impl.command.AuthenticationCommand;
import ru.gb.pugacheva.server.service.impl.command.ListCreatingCommand;
import ru.gb.pugacheva.server.service.impl.command.ViewFilesInDirCommand;

import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class Factory {

   public static ServerService getServerService (){
        return new NettyServerService();
    }

    public static CommandDictionaryService getCommandDictionaryService(){
        return new CommandDictionaryServiceImpl();
    }

    public static List<CommandService> getCommandServices (){
        return Arrays.asList(new AuthenticationCommand(), new ListCreatingCommand());
    }

    public static DatabaseConnectionService getDatabaseConnectionService(){
        return new DatabaseConnectionServiceImpl();
    }

    public static AuthenticationService getAuthenticationService(){
        return new AuthenticationServiceImpl();
    }


    public static ListOfFilesService getListOfFilesService(){
        return new ListOfFilesService();
    }
}

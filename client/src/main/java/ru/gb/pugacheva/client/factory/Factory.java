package ru.gb.pugacheva.client.factory;

import ru.gb.pugacheva.client.service.CommandDictionaryService;
import ru.gb.pugacheva.client.service.CommandService;
import ru.gb.pugacheva.client.service.NetworkService;
import ru.gb.pugacheva.client.service.impl.ClientCommandDictionaryServiceImpl;
import ru.gb.pugacheva.client.service.impl.NettyNetworkService;
import ru.gb.pugacheva.client.service.impl.command.AcceptedLoginCommand;
import ru.gb.pugacheva.client.service.impl.command.CloudFilesListCommand;
import ru.gb.pugacheva.client.service.impl.command.FailedLoginCommand;

import java.util.Arrays;
import java.util.List;

public class Factory {

//    public static NetworkService getNetworkService(){
//        return IONetworkService.getInstance();
//    }

//    public static NetworkService getNetworkService(){
//        return new NettyNetworkService();
//    } // стартовый мой вариант сети с Нетти

    public static NetworkService initializeNetworkService() {
        return NettyNetworkService.initializeNetwork();
    }

    public static NetworkService getNetworkService() {
        return NettyNetworkService.getNetwork();
    }

    public static CommandDictionaryService getCommandDictionary() {
        return new ClientCommandDictionaryServiceImpl();
    }


    public static List<CommandService> getCommandServices() {
        return Arrays.asList(new AcceptedLoginCommand(), new FailedLoginCommand(), new CloudFilesListCommand()); // команды добавлять
    }

     //TODO для клиента добавить сюда создание словаря команд по аналогии с сервером

}

package ru.gb.pugacheva.client.factory;

import ru.gb.pugacheva.client.service.Callback;
import ru.gb.pugacheva.client.service.CommandDictionaryService;
import ru.gb.pugacheva.client.service.CommandService;
import ru.gb.pugacheva.client.core.NetworkService;
import ru.gb.pugacheva.client.service.impl.ClientCommandDictionaryServiceImpl;
import ru.gb.pugacheva.client.core.NettyNetworkService;
import ru.gb.pugacheva.client.service.impl.command.AcceptedLoginCommand;
import ru.gb.pugacheva.client.service.impl.command.CloudFilesListCommand;
import ru.gb.pugacheva.client.service.impl.command.FailedLoginCommand;
import ru.gb.pugacheva.client.service.impl.command.UploadFileCommand;

import java.util.Arrays;
import java.util.List;

public class Factory {

    public static NetworkService initializeNetworkService(Callback setButtonsAbleCallback) {
        return NettyNetworkService.initializeNetwork(setButtonsAbleCallback);
    }

    public static NetworkService getNetworkService() {
        return NettyNetworkService.getNetwork();
    }

    public static CommandDictionaryService getCommandDictionary() {
        return new ClientCommandDictionaryServiceImpl();
    }

    public static List<CommandService> getCommandServices() {
        return Arrays.asList(new AcceptedLoginCommand(), new FailedLoginCommand(),
                new CloudFilesListCommand(), new UploadFileCommand());
    }

}

package ru.gb.pugacheva.client.factory;

import ru.gb.pugacheva.client.service.Callback;
import ru.gb.pugacheva.client.service.CommandDictionaryService;
import ru.gb.pugacheva.client.service.CommandService;
import ru.gb.pugacheva.client.core.NetworkService;
import ru.gb.pugacheva.client.service.impl.ClientCommandDictionaryServiceImpl;
import ru.gb.pugacheva.client.core.NettyNetworkService;
import ru.gb.pugacheva.client.service.impl.ui_command.*;

import java.util.Arrays;
import java.util.List;

public class Factory {

    public static NetworkService initializeNetworkService(Callback setButtonsAbleCallback) {
        return NettyNetworkService.initializeNetwork(setButtonsAbleCallback);
    }

    public static CommandDictionaryService getCommandDictionary() {
        return new ClientCommandDictionaryServiceImpl();
    }

    public static List<CommandService> getCommandServices() {
        return Arrays.asList(new AcceptedLoginCommand(), new FailedLoginCommand(),
                new CloudFilesListCommand(), new UploadFileCommand());
    }

}

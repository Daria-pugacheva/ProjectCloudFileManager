package ru.gb.pugacheva.server.service.impl;

import ru.gb.pugacheva.server.factory.Factory;
import ru.gb.pugacheva.server.service.CommandDictionaryService;
import ru.gb.pugacheva.server.service.CommandService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandDictionaryServiceImpl implements CommandDictionaryService {

    private final Map<String, CommandService> commandDictionary;

    public CommandDictionaryServiceImpl() {
        this.commandDictionary = Collections.unmodifiableMap(getCommandDictionary());
    }

    private Map <String, CommandService> getCommandDictionary() {
        List<CommandService> commandServices = Factory.getCommandServices();
        Map <String , CommandService> commandDictionary = new HashMap<>();
        for (CommandService commandService : commandServices) {
            commandDictionary.put(commandService.getCommand(), commandService);
        }
        return commandDictionary;
    }

    @Override
    public String processCommand(String command) {
        String [] commandParts = command.split("\\s");

        if (commandParts.length>0 && commandDictionary.containsKey(commandParts[0])){
            return commandDictionary.get(commandParts[0]).processCommand(command);
        }
        return "Error command";
    }
}

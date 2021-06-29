package ru.gb.pugacheva.server.service.impl.command;

import ru.gb.pugacheva.common.domain.Command;
import ru.gb.pugacheva.common.domain.FileInfo;
import ru.gb.pugacheva.server.factory.Factory;
import ru.gb.pugacheva.server.service.CommandService;
import ru.gb.pugacheva.server.service.impl.ListOfFilesService;

import java.util.List;

public class ListCreatingCommand implements CommandService<List<FileInfo>> {

    private ListOfFilesService listOfFilesService;

    @Override
    public List<FileInfo> processCommand(Command command) {
        final int requirementCountCommandArgs = 1;
        if (command.getArgs().length != requirementCountCommandArgs) {
            throw new IllegalArgumentException("Command " + getCommand() + "is not correct");
        }
        return process((String) command.getArgs()[0]);
    }

    private List<FileInfo> process(String login) {
        listOfFilesService = Factory.getListOfFilesService();
        return listOfFilesService.createServerFilesList(login);
    }

    @Override
    public String getCommand() {
        return "createFilesList";
    }
}

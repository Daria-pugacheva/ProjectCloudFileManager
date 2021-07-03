package ru.gb.pugacheva.server.service.impl.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.gb.pugacheva.common.domain.Command;
import ru.gb.pugacheva.common.domain.CommandType;
import ru.gb.pugacheva.common.domain.FileInfo;
import ru.gb.pugacheva.server.factory.Factory;
import ru.gb.pugacheva.server.service.CommandService;
import ru.gb.pugacheva.server.service.impl.ListOfClientFilesInCloudCreatingService;

import java.util.List;

public class ListCreatingCommand implements CommandService {

    private ListOfClientFilesInCloudCreatingService listOfClientFilesInCloudCreatingService;
    private static final Logger LOGGER = LogManager.getLogger(ListCreatingCommand.class);


    @Override
    public List<FileInfo> processCommand(Command command) {
        final int requirementCountCommandArgs = 1;
        if (command.getArgs().length != requirementCountCommandArgs) {
            LOGGER.error("Command " + getCommand() + "is not correct");
            throw new IllegalArgumentException("Command " + getCommand() + "is not correct");
        }
        return process((String) command.getArgs()[0]);
    }

    private List<FileInfo> process(String login) {
        listOfClientFilesInCloudCreatingService = Factory.getListOfFilesService();
        return listOfClientFilesInCloudCreatingService.createServerFilesList(login);
    }

    @Override
    public String getCommand() {
        return CommandType.FILESLIST.toString();
    }
}

package ru.gb.pugacheva.client.service.impl.ui_command;

import ru.gb.pugacheva.client.MainClientApp;
import ru.gb.pugacheva.client.controller.Controller;
import ru.gb.pugacheva.client.service.CommandService;
import ru.gb.pugacheva.common.domain.Command;
import ru.gb.pugacheva.common.domain.CommandType;
import ru.gb.pugacheva.common.domain.FileInfo;

import java.util.List;

public class CloudFilesListCommand implements CommandService {

    @Override
    public void processCommand(Command command) {
        Controller currentController = (Controller) MainClientApp.getActiveController();

        currentController.createServerListFilesOnGUI((String) command.getArgs()[0], (List<FileInfo>) command.getArgs()[1]);
    }

    @Override
    public String getCommand() {
        return CommandType.CLOUD_FILESLIST.toString();
    }
}

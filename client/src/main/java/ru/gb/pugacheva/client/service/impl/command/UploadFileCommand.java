package ru.gb.pugacheva.client.service.impl.command;

import ru.gb.pugacheva.client.MainClientApp;
import ru.gb.pugacheva.client.controller.Controller;
import ru.gb.pugacheva.client.service.CommandService;
import ru.gb.pugacheva.common.domain.Command;
import ru.gb.pugacheva.common.domain.CommandType;

public class UploadFileCommand implements CommandService {

    @Override
    public void processCommand(Command command) {
        Controller currentController = (Controller) MainClientApp.getActiveController();
        currentController.sendFile((String) command.getArgs()[1]);
    }

    @Override
    public String getCommand() {
        return CommandType.READY_TO_UPLOAD.toString();
    }
}

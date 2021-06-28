package ru.gb.pugacheva.client.service.impl.command;

import ru.gb.pugacheva.client.MainClientApp;
import ru.gb.pugacheva.client.controller.Controller;
import ru.gb.pugacheva.client.service.CommandService;
import ru.gb.pugacheva.common.domain.Command;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UploadFileCommand implements CommandService {

    @Override
    public void processCommand(Command command) {
        System.out.println("7. Запущена команда UploadFileCommand (readyToUpload) для файла" + (String) command.getArgs()[1]);
        Controller currentController = (Controller) MainClientApp.getActiveController();
        currentController.getNetworkService().sendFile((String) command.getArgs()[1]);

    }

    @Override
    public String getCommand() {
        return "readyToUpload";
    }
}
package ru.gb.pugacheva.client.service.impl.command;

import javafx.application.Platform;
import ru.gb.pugacheva.client.MainClientApp;
import ru.gb.pugacheva.client.controller.Controller;
import ru.gb.pugacheva.client.service.CommandService;
import ru.gb.pugacheva.common.domain.Command;

public class AcceptedLoginCommand implements CommandService {

    @Override
    public void processCommand(Command command) {
        Controller currentController = (Controller) MainClientApp.getActiveController();
        currentController.setLogin((String) command.getArgs()[0]);
        Platform.runLater(() -> currentController.loginPanel.setVisible(false));
        Platform.runLater(() -> currentController.workPanel.setVisible(true));
        String[] args = {currentController.getLogin()};
        currentController.getNetworkService().sendCommand(new Command("filesList", args));
    }

    @Override
    public String getCommand() {
        return "loginOK";
    }
}

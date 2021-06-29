package ru.gb.pugacheva.client.service.impl.command;

import javafx.application.Platform;
import ru.gb.pugacheva.client.MainClientApp;
import ru.gb.pugacheva.client.controller.Controller;
import ru.gb.pugacheva.client.service.CommandService;
import ru.gb.pugacheva.common.domain.Command;

public class FailedLoginCommand implements CommandService {

    @Override
    public void processCommand(Command command) {
        Controller currentController = (Controller) MainClientApp.getActiveController();
        Platform.runLater(() -> currentController.createAlert("Такой логин уже существует" +
                " с другим паролем. Введите другую пару логин/пароль для регистрации"));
    }

    @Override
    public String getCommand() {
        return "registrationFailed";
    }
}

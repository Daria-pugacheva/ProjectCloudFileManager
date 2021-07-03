package ru.gb.pugacheva.client.service.impl.command;

import ru.gb.pugacheva.client.MainClientApp;
import ru.gb.pugacheva.client.controller.Controller;
import ru.gb.pugacheva.client.service.CommandService;
import ru.gb.pugacheva.common.domain.Command;
import ru.gb.pugacheva.common.domain.CommandType;

public class AcceptedLoginCommand implements CommandService {

    @Override
    public void processCommand(Command command) {
        Controller currentController = (Controller) MainClientApp.getActiveController();

        currentController.setLogin((String) command.getArgs()[0]);
        currentController.changeLoginPanelToWorkPanel();

        String[] args = {currentController.getLogin()};
        currentController.sendCommand(new Command(CommandType.FILESLIST.toString(), args));
    }

    @Override
    public String getCommand() {
        return CommandType.LOGIN_OK.toString();
    }
}

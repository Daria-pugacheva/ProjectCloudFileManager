package ru.gb.pugacheva.server.service;

import ru.gb.pugacheva.common.domain.Command;

public interface CommandService {

    Object processCommand(Command command);

    String getCommand();

}

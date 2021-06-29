package ru.gb.pugacheva.client.service;

import ru.gb.pugacheva.common.domain.Command;

public interface CommandService {

    void processCommand(Command command);

    String getCommand();

}

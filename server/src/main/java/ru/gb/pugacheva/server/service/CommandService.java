package ru.gb.pugacheva.server.service;

import ru.gb.pugacheva.common.domain.Command;

public interface CommandService <T> {

     T processCommand (Command command);

    String getCommand();

}

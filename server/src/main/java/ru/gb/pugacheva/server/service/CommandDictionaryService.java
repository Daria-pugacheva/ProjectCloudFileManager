package ru.gb.pugacheva.server.service;

import ru.gb.pugacheva.common.domain.Command;

public interface CommandDictionaryService {

    Object processCommand (Command command);
}

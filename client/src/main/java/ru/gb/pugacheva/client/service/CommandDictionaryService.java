package ru.gb.pugacheva.client.service;

import ru.gb.pugacheva.common.domain.Command;

public interface CommandDictionaryService {

    void processCommand(Command command);

}

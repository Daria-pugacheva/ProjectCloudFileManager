package ru.gb.pugacheva.client.service;

import ru.gb.pugacheva.common.domain.Command;

public interface CommandService { // может, придется делать обобщенным

    void processCommand(Command command); // и может, здесь будет не void

    String getCommand();

}

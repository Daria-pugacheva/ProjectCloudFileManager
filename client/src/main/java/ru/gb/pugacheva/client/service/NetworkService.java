package ru.gb.pugacheva.client.service;

import ru.gb.pugacheva.common.domain.Command;

public interface NetworkService {

    void sendCommand (Command command);

    Object  readCommandResult (); //  в примере с урока тут String

    void closeConnection();

    boolean isConnected();
}

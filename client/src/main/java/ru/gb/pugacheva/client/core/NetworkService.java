package ru.gb.pugacheva.client.core;

import ru.gb.pugacheva.common.domain.Command;

public interface NetworkService {

    void sendCommand (Command command);

    void closeConnection();

    boolean isConnected();

    void sendFile (String pathToFile);

}

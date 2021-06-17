package ru.gb.pugacheva.client.service;

public interface NetworkService {

    void sendCommand (String command);

    int readCommandResult (byte [] buffer);

    void closeConnection();
}

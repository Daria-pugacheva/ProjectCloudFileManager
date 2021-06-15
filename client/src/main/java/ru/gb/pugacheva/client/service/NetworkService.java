package ru.gb.pugacheva.client.service;

public interface NetworkService {

    void sendCommand (String command);

    String readCommandResult ();

    void closeConnection();
}

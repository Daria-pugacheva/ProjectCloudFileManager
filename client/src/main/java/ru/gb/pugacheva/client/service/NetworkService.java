package ru.gb.pugacheva.client.service;

import io.netty.channel.socket.SocketChannel;
import ru.gb.pugacheva.common.domain.Command;

import java.io.File;
import java.net.Socket;

public interface NetworkService {

    void sendCommand (Command command);

    Object  readCommandResult (); //  в примере с урока тут String

    void closeConnection();

    boolean isConnected();

    void sendFile (String pathToFile);

   // SocketChannel getChannel();
}

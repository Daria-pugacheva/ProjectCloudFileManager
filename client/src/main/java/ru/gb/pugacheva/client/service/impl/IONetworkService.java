package ru.gb.pugacheva.client.service.impl;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import ru.gb.pugacheva.client.service.NetworkService;
import ru.gb.pugacheva.common.domain.Command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class IONetworkService implements NetworkService {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8189;
    private static IONetworkService instance;

    private static Socket socket;
    private static ObjectDecoderInputStream in;
    private static ObjectEncoderOutputStream out;


    private IONetworkService(){}

    public static IONetworkService getInstance(){
        if(instance==null){
            instance = new IONetworkService();
            initializeSocket ();
            initializeIOStreams ();

        }
        return instance;
    }

    private static void initializeIOStreams() {
        try {
            in = new ObjectDecoderInputStream(socket.getInputStream());
            out = new ObjectEncoderOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initializeSocket() {
        try {
            socket = new Socket(SERVER_HOST,SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendCommand(Command command) {
        try {
            out.writeObject(command);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFile(File file){
        try {
            out.writeObject(file);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object readCommandResult() { //  в примере с урока тут String
        try {
            return in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Read command result exception" + e.getMessage());
        }
    }

    @Override
    public void closeConnection() {
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

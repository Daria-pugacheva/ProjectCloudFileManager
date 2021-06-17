package ru.gb.pugacheva.client.service.impl;

import ru.gb.pugacheva.client.service.NetworkService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class IONetworkService implements NetworkService {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8189;
    private static IONetworkService instance;

    private static Socket socket;
    private static DataInputStream in;
    private static DataOutputStream out;


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
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
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
    public void sendCommand(String command) {
        try {
            out.write(command.getBytes(StandardCharsets.UTF_8));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int readCommandResult(byte [] buffer) {
        try {
            return in.read(buffer);
        } catch (IOException e) {
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

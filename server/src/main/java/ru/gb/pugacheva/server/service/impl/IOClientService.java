package ru.gb.pugacheva.server.service.impl;

import ru.gb.pugacheva.server.service.ClientService;
import ru.gb.pugacheva.server.service.CommandDictionaryService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class IOClientService implements ClientService {

    private final Socket clientSocket;
    private final CommandDictionaryService dictionaryService;

    private DataInputStream in;
    private DataOutputStream out;

    public IOClientService(Socket clientSocket, CommandDictionaryService dictionaryService) {
        this.clientSocket = clientSocket;
        this.dictionaryService = dictionaryService;

        initializeIOStreams();
    }

    private void initializeIOStreams() {
        try {
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startIOProcess() {
        new Thread(() -> {
            try {
                while (true) {
                    String clientCommand = readCommand();
                    String commandResult = dictionaryService.processCommand(clientCommand);
                    writeCommandResult(commandResult);
                }
            }finally {
                closeConnection();
            }
        }).start();


    }

    private void writeCommandResult (String commandResult){
        try {
            out.writeUTF(commandResult);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readCommand() {
        try {
            return in.readUTF();
        } catch (IOException e) {
            throw new RuntimeException("Read command result exception: " + e.getMessage());
        }
    }

    private void closeConnection (){
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
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

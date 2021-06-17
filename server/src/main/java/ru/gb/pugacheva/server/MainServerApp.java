package ru.gb.pugacheva.server;

import ru.gb.pugacheva.server.factory.Factory;

public class MainServerApp {

    public static void main(String[] args) {

        Factory.getServerService().startServer();

    }
}

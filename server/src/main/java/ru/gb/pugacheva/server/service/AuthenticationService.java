package ru.gb.pugacheva.server.service;

public interface AuthenticationService {

    void open();

    boolean isClientRegistered(String login, String password);

    boolean isLoginBusy (String login);

    boolean registerClient (String login, String password);

    void close ();



}

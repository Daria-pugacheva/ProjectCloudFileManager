package ru.gb.pugacheva.server.service;

import java.sql.Statement;

public interface DatabaseConnectionService {

    Statement getStmt();

    void closeConnection();

}

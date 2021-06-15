package ru.gb.pugacheva.server.service;

import java.sql.Statement;

public interface DatabaseConnectionService {

   // void openConnection ();
   Statement getStmt();

    void closeConnection ();


}

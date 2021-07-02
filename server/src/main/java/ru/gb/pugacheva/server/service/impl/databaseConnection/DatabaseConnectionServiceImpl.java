package ru.gb.pugacheva.server.service.impl.databaseConnection;

import ru.gb.pugacheva.common.domain.PropertiesReciever;
import ru.gb.pugacheva.server.service.DatabaseConnectionService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnectionServiceImpl implements DatabaseConnectionService {

    private Connection connection;
    private Statement stmt;

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public Statement getStmt() {
        return stmt;
    }

    public DatabaseConnectionServiceImpl() {
        try {
            //Class.forName("org.sqlite.JDBC");
            Class.forName(PropertiesReciever.getProperties("dbDriver"));
            //this.connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            this.connection = DriverManager.getConnection(PropertiesReciever.getProperties("dbURL"));
            this.stmt = connection.createStatement();
            System.out.println("Сервер подключен к БД");
        } catch (ClassNotFoundException | SQLException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException("Не удается подключиться к базе данных");
        }
    }

    @Override
    public void closeConnection() {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}

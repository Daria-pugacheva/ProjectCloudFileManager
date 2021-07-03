package ru.gb.pugacheva.server.service.impl.databaseConnection;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.gb.pugacheva.common.domain.PropertiesReciever;
import ru.gb.pugacheva.server.service.DatabaseConnectionService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnectionServiceImpl implements DatabaseConnectionService {

    private Connection connection;
    private Statement stmt;

    private static final Logger LOGGER = LogManager.getLogger(DatabaseConnectionServiceImpl.class);

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
            Class.forName(PropertiesReciever.getProperties("dbDriver"));
            this.connection = DriverManager.getConnection(PropertiesReciever.getProperties("dbURL"));
            this.stmt = connection.createStatement();
            LOGGER.info("Сервер подключен к базе данных");
        } catch (ClassNotFoundException | SQLException throwables) {
            LOGGER.throwing(Level.ERROR,throwables);
            throw new RuntimeException("Не удается подключиться к базе данных");
        }
    }

    @Override
    public void closeConnection() {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                LOGGER.throwing(Level.ERROR,e);
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                LOGGER.throwing(Level.ERROR,e);
            }
        }
    }

}

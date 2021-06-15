package ru.gb.pugacheva.server.service.impl;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import ru.gb.pugacheva.server.factory.Factory;
import ru.gb.pugacheva.server.service.AuthenticationService;
import ru.gb.pugacheva.server.service.DatabaseConnectionService;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthenticationServiceImpl implements AuthenticationService {

    private DatabaseConnectionService dbConnection;

    @Override
    public void open() {
        dbConnection = Factory.getDatabaseConnectionService();
    }

    @Override
    public boolean isClientRegistered(String login, String password) {
        String query = String.format("select id from clients where login = '%s' and pass = '%s';", login,password);
        try (ResultSet rs = dbConnection.getStmt().executeQuery(query)) {
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isLoginBusy (String login) {
        String query = String.format("select id from clients where login = '%s';", login);
        try (ResultSet rs = dbConnection.getStmt().executeQuery(query)) {
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
           e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean registerClient(String login, String password) {
        if(!isClientRegistered(login,password) && !isLoginBusy(login)) {
            String query = String.format("insert into clients set (login, pass) values ('%s','%s');", login, password);
            try {
                dbConnection.getStmt().executeQuery(query);
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else{
            Alert choseAnotherLogin = new Alert(Alert.AlertType.WARNING,"Такой логин уже существует" +
                    " с другим паролем. Введите другую пару логин/пароль для регистрации", ButtonType.OK);
            choseAnotherLogin.showAndWait();
        }
        return false;
    }


    @Override
    public void close() {
        dbConnection.closeConnection();
    }
}

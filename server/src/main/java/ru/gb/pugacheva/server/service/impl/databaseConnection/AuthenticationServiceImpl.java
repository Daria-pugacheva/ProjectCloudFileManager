package ru.gb.pugacheva.server.service.impl.databaseConnection;

import ru.gb.pugacheva.server.core.NettyServerService;
import ru.gb.pugacheva.server.service.AuthenticationService;
import ru.gb.pugacheva.server.service.DatabaseConnectionService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthenticationServiceImpl implements AuthenticationService {

    private DatabaseConnectionService dbConnection;
    private PreparedStatement registrationSearching;
    private PreparedStatement loginChecking;
    private PreparedStatement newClientCreation;

    public AuthenticationServiceImpl (){
        this.dbConnection = NettyServerService.getDatabaseConnectionService();
        createPreparedStatements();
    }

    private void createPreparedStatements() {
        try {
            this.registrationSearching = dbConnection.getConnection().prepareStatement("select id from clients where login = ? and pass = ?;");
            this.loginChecking = dbConnection.getConnection().prepareStatement("select id from clients where login = ?;");
            this.newClientCreation = dbConnection.getConnection().prepareStatement("insert into clients (login, pass) values (?,?);");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    @Override
    public boolean isClientRegistered(String login, String password) {
        try {
            registrationSearching.setString(1, login);
            registrationSearching.setString(2, password);
            try (ResultSet rs = registrationSearching.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        }catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isLoginBusy(String login) {
        try {
            loginChecking.setString(1, login);
            try (ResultSet rs = loginChecking.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        }catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean registerClient(String login, String password) {
        if (!isClientRegistered(login, password) && !isLoginBusy(login)) {
            try {
                newClientCreation.setString(1,login);
                newClientCreation.setString(2,password);
                newClientCreation.executeUpdate();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (isLoginBusy(login)) {
            System.out.println("логин занят");
        }
        return false;
    }

}

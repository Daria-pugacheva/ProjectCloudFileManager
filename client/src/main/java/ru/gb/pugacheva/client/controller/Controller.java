package ru.gb.pugacheva.client.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import ru.gb.pugacheva.client.factory.Factory;
import ru.gb.pugacheva.client.core.NetworkService;
import ru.gb.pugacheva.common.domain.Command;
import ru.gb.pugacheva.common.domain.FileInfo;
import ru.gb.pugacheva.common.domain.PropertiesReciever;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Controller implements Initializable {

    public HBox loginPanel;
    public HBox workPanel;

    public TextField loginField;
    public TextField clientPathToFile;
    public TextField serverPathToFile;

    public PasswordField passwordField;

    public TableView<FileInfo> clientFiles;
    public TableView<FileInfo> serverFiles;

    public Button downloadButton;
    public Button uploadButton;

    private NetworkService networkService;

    private String login = null;

    public NetworkService getNetworkService() {
        return networkService;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        workPanel.setVisible(false);
        initializeNetworkService();

        makeClientTable();
        makeServerTable();

        createClientListFiles(Paths.get(PropertiesReciever.getProperties("clientDirectory")));
    }

    private void initializeNetworkService(){
        networkService = Factory.initializeNetworkService(()->{
            downloadButton.setDisable(false);
            uploadButton.setDisable(false);
            createClientListFiles(Paths.get(clientPathToFile.getText()));
        });
    }

    private void makeClientTable() {
        TableColumn<FileInfo, String> clientFileTypeColumn = new TableColumn<>("Тип"); //TODO: декомпозировать код
        clientFileTypeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFileType().getName()));
        clientFileTypeColumn.setPrefWidth(48);

        TableColumn<FileInfo, String> clientFileNameColumn = new TableColumn<>("Имя файла");
        clientFileNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFileName()));
        clientFileNameColumn.setPrefWidth(240);

        TableColumn<FileInfo, Long> clientFileSizeColumn = new TableColumn<>("Размер файла");
        clientFileSizeColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSize()));
        clientFileSizeColumn.setPrefWidth(120);
        clientFileSizeColumn.setCellFactory(column -> {
            return new TableCell<FileInfo, Long>() {
                @Override
                protected void updateItem(Long item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        String text = String.format("%,d bytes", item);
                        if (item == -1L) {
                            text = "DIR";
                        }
                        setText(text);
                    }
                }
            };
        });

        clientFiles.getColumns().addAll(clientFileTypeColumn, clientFileNameColumn, clientFileSizeColumn);

        moveIntoDirectory(clientFiles, clientPathToFile);
    }

    private void makeServerTable() {
        TableColumn<FileInfo, String> clientFileTypeColumn = new TableColumn<>("Тип"); //TODO: декомпозировать код
        clientFileTypeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFileType().getName()));
        clientFileTypeColumn.setPrefWidth(48);

        TableColumn<FileInfo, String> clientFileNameColumn = new TableColumn<>("Имя файла");
        clientFileNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFileName()));
        clientFileNameColumn.setPrefWidth(240);

        TableColumn<FileInfo, Long> clientFileSizeColumn = new TableColumn<>("Размер файла");
        clientFileSizeColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSize()));
        clientFileSizeColumn.setPrefWidth(120);
        clientFileSizeColumn.setCellFactory(column -> {
            return new TableCell<FileInfo, Long>() {
                @Override
                protected void updateItem(Long item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        String text = String.format("%,d bytes", item);
                        if (item == -1L) {
                            text = "DIR";
                        }
                        setText(text);
                    }
                }
            };
        });

        serverFiles.getColumns().addAll(clientFileTypeColumn, clientFileNameColumn, clientFileSizeColumn);
    }

    private void moveIntoDirectory(TableView<FileInfo> tableView, TextField textField) {
        tableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    Path currentPath = Paths.get(textField.getText());
                    Path newPath = currentPath.resolve(tableView.getSelectionModel().getSelectedItem().getFileName());
                    if (Files.isDirectory(newPath)) {
                        createClientListFiles(newPath);
                    }
                }

            }
        });
    }

    public void createClientListFiles(Path path) {
        try {
            clientPathToFile.setText(path.normalize().toAbsolutePath().toString());
            clientFiles.getItems().clear();
            clientFiles.getItems().addAll(Files.list(path).map(FileInfo::new).collect(Collectors.toList()));
            clientFiles.sort();

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Не удалось обновить список файлов", ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void createServerListFiles(String path, List<FileInfo> list) {
        System.out.println("в методе по заполнению окна клиента лист  " + list);
        serverPathToFile.clear();
        serverPathToFile.setText(path);
        serverFiles.getItems().clear();
        serverFiles.getItems().addAll(list);
        serverFiles.sort();
    }

    public void clientMoveUpInFilePath(ActionEvent actionEvent) {
        Path currentPath = Paths.get(clientPathToFile.getText());
        Path upperPath = currentPath.getParent();
        if (upperPath != null) {
            createClientListFiles(upperPath);
        }
    }

    public void createAlert(String text) {
        Alert alert = new Alert(Alert.AlertType.WARNING, text, ButtonType.OK);
        alert.showAndWait();
    }

    public void shutdown() {
        networkService.closeConnection();
    }

    public void login(ActionEvent actionEvent) {
        if (!networkService.isConnected()) {
            initializeNetworkService();
        }
        String[] textCommand = {"login", loginField.getText(), passwordField.getText()};
        if (textCommand.length > 2) {
            String[] commandArgs = Arrays.copyOfRange(textCommand, 1, textCommand.length);
            networkService.sendCommand(new Command(textCommand[0], commandArgs));
        } else {
            createAlert("Не все поля для регистрации заполнены: введите логин и пароль");
        }
        loginField.clear();
        passwordField.clear();
    }

    public void download(ActionEvent actionEvent) {
        uploadButton.setDisable(true); //TODO: ДОДЕЛАТЬ, ЧТОБЫ БЛОКИРОВКА КНОПОК СНИМАЛАСЬ, ЕСЛИ ПРОБЛЕМА С ФАЙЛОМ.
        downloadButton.setDisable(true);
        if (!networkService.isConnected()) {
            initializeNetworkService();
        }
        Long fileSize = serverFiles.getSelectionModel().getSelectedItem().getSize();
        String userDirectoryForDounload = clientPathToFile.getText();
        Object[] commandArgs = {getSelectedFilename(serverFiles), login, fileSize, userDirectoryForDounload};
        Command command = new Command("download", commandArgs);
        networkService.sendCommand(command);
        System.out.println("1.Нажали на кнопку и из хэндлера отправли команду download" + Arrays.asList(commandArgs));
    }

    public void upload(ActionEvent actionEvent) {
        uploadButton.setDisable(true); //TODO: ДОДЕЛАТЬ, ЧТОБЫ БЛОКИРОВКА КНОПОК СНИМАЛАСЬ, ЕСЛИ ПРОБЛЕМА С ФАЙЛОМ.
        downloadButton.setDisable(true);
        if (!networkService.isConnected()) {
            initializeNetworkService();
        }
        String absolutePathOfUploadFile = getcurrentPath(clientPathToFile) + "\\" + getSelectedFilename(clientFiles); //TODO: смотреть, почему файл не находится, когда проваливаюсь в папки.
        Long fileSize = clientFiles.getSelectionModel().getSelectedItem().getSize();
        Object[] commandArgs = {getSelectedFilename(clientFiles), absolutePathOfUploadFile, login, fileSize};
        Command command = new Command("upload", commandArgs);
        networkService.sendCommand(command);
        System.out.println("1.Нажали на кнопку и из хэндлера отправли команду upload" + getSelectedFilename(clientFiles) + absolutePathOfUploadFile + login);
    }

    public String getSelectedFilename(TableView<FileInfo> tableView) {
        if (!tableView.isFocused()) {
            return null;
        }
        return tableView.getSelectionModel().getSelectedItem().getFileName();
    }

    public String getcurrentPath(TextField textField) {
        return textField.getText();
    }

}
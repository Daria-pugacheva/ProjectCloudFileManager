package ru.gb.pugacheva.client.controller;

import javafx.application.Platform;
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
import ru.gb.pugacheva.client.service.NetworkService;
import ru.gb.pugacheva.client.service.impl.NettyNetworkService;
import ru.gb.pugacheva.common.domain.Command;
import ru.gb.pugacheva.common.domain.FileInfo;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Controller implements Initializable {

    public HBox loginPanel, workPanel;
    public TextField loginField, clientPathToFile, serverPathToFile;
    public PasswordField passwordField;
    @FXML
    TableView<FileInfo> clientFiles, serverFiles;
    public Button downloadButton, uploadButton;

    private NetworkService networkService;

    private String login = null;

    public NetworkService getNetworkService() {
        return networkService;
    }

    public void setNetworkService(NetworkService networkService) {
        this.networkService = networkService;
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

       // networkService = Factory.getNetworkService(); // мой вариантTODO: возможно, момент подключения лучше перенести на кнопку регистрации?

        networkService = Factory.initializeNetworkService(); // DEN

        makeClientTable();
        makeServerTable();

        createClientListFiles(Paths.get("C:/"));  // выводим изначально систему от диска С. МОЖЕТ, перенести на авторизацию

        //createCommandResultHandler(); пока уберем

    }

    private void makeClientTable() {
        TableColumn<FileInfo, String> clientFileTypeColumn = new TableColumn<>("Тип"); //TODO: упаковать в метод + добавить формирование таблицы в части серверной
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

        moveIntoDirectory(clientFiles,clientPathToFile);

//        clientFiles.setOnMouseClicked(new EventHandler<MouseEvent>() { //TODO: вынести отдельным методом(общим со стр.133)
//            @Override
//            public void handle(MouseEvent event) {
//                if (event.getClickCount() == 2) {
//                    Path currentPath = Paths.get(clientPathToFile.getText());
//                    Path newPath = currentPath.resolve(clientFiles.getSelectionModel().getSelectedItem().getFileName());
//                    if (Files.isDirectory(newPath)) {
//                        createClientListFiles(newPath);
//                    }
//                }
//
//            }
//        });
    }

    private void moveIntoDirectory (TableView <FileInfo> tableView, TextField textField ){
        tableView.setOnMouseClicked(new EventHandler<MouseEvent>() { //TODO: вынести отдельным методом(общим со стр.133)
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

    private void makeServerTable() {
        TableColumn<FileInfo, String> clientFileTypeColumn = new TableColumn<>("Тип");
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

        moveIntoDirectory(serverFiles,serverPathToFile); // возможно, не надо. Смотреть

    }

    public void createClientListFiles(Path path) { // ЗАВЕСТИ ОТДЕЛЬНЫЙ КЛАСС/Интерфейс, который отвечает за заполнение таблицы
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

    public void createServerListFiles(String path, List<FileInfo> list) { // ЗАВЕСТИ ОТДЕЛЬНЫЙ КЛАСС/Интерфейс, который отвечает за заполнение таблицы
        System.out.println("в методе по заполнению окна клиента лист  " + list);
        serverPathToFile.clear();
        serverPathToFile.setText(path);
        serverFiles.getItems().clear();//+
        serverFiles.getItems().addAll(list);//+
        serverFiles.sort();//+
    }

    public void clientMoveUpInFilePath(ActionEvent actionEvent) {
        Path currentPath = Paths.get(clientPathToFile.getText());
        Path upperPath = currentPath.getParent();
        if (upperPath != null) {
            createClientListFiles(upperPath);
        }
    }

    public void createAlert (String text){
        Alert alert = new Alert(Alert.AlertType.WARNING, text, ButtonType.OK);
        alert.showAndWait();

    }

    public void shutdown() {
        networkService.closeConnection();
    }

    public void login(ActionEvent actionEvent) {  //отправка на сервер логина и пароля
        if (!networkService.isConnected()) {
            networkService = Factory.initializeNetworkService();
        }
        String[] textCommand = {"login", loginField.getText(), passwordField.getText()};
        if (textCommand.length > 2) {
            String[] commandArgs = Arrays.copyOfRange(textCommand, 1, textCommand.length);
            networkService.sendCommand(new Command(textCommand[0], commandArgs));
        }else{
            createAlert("Не все поля для регистрации заполнены: введите логин и пароль");
        }
        // networkService.sendCommand("login " + loginField.getText() + " " + passwordField.getText());
        loginField.clear();
        passwordField.clear();
    }



    public void download(ActionEvent actionEvent) {
        if (!networkService.isConnected()) {
            networkService = Factory.initializeNetworkService();
        }

    }

    public void upload(ActionEvent actionEvent) {
        uploadButton.setDisable(true); //TODO: ДОДЕЛАТЬ, ЧТОБЫ БЛОКИРОВКА КНОПОК СНИМАЛАСЬ, ЕСЛИ ПРОБЛЕМА С ФАЙЛОМ.
        downloadButton.setDisable(true);
        if (!networkService.isConnected()) {
            networkService = Factory.initializeNetworkService();
        }
        String absolutePathOfUploadFile = getcurrentPath(clientPathToFile) + getSelectedFilename(clientFiles); //TODO: смотреть, почему файл не находится, когда проваливаюсь в папки.
        Long fileSize = clientFiles.getSelectionModel().getSelectedItem().getSize();
        Object [] commandArgs = {getSelectedFilename(clientFiles), absolutePathOfUploadFile,login,fileSize};
        Command command = new Command("upload",commandArgs);
        networkService.sendCommand(command); // на сервер upload + имя файла
        System.out.println("1.Нажали на кнопку и из хэндлера отправли команду upload" + getSelectedFilename(clientFiles)+ absolutePathOfUploadFile+login);
    }

    public String getSelectedFilename (TableView <FileInfo> tableView){ // указываем, с какой панелью имеем дело
        if (!tableView.isFocused()){
            return null;
        }
        return tableView.getSelectionModel().getSelectedItem().getFileName();
    }

    public String getcurrentPath (TextField textField){
        return textField.getText();
    }


//clientFiles.getSelectionModel().getSelectedItem().getFileName(); - последовательность для поиска имени файла

 //   private void createCommandResultHandler() {
//        new Thread(() -> {
//
//            // byte [] buffer = new byte [1024]; / убрать. это было для айт-буффера
//            //в этом цикле по-идее происходит авторизация
//            while (true) {
//                Object obj = networkService.readCommandResult();
//                if (obj.getClass().equals(String.class)) {
//                    String resultCommand = (String) obj; // каст, потому что знаем, что тут авторизация и будет строка
////                int bytesFromBuffer = networkService.readCommandResult(buffer); //убрать , был вариант с байт-буффером
////                String resultCommand = new String(buffer,0,bytesFromBuffer); //убрать , был вариант с байт-буффером
////  }
//                    if (resultCommand.startsWith("registrationOK") || resultCommand.startsWith("loginOK")) {
//                        login = resultCommand.split("\\s")[1];
//                        Platform.runLater(() -> loginPanel.setVisible(false));
//                        Platform.runLater(() -> workPanel.setVisible(true));
//
//                        String[] args = {login};
//                        networkService.sendCommand(new Command("filesList", args));
//                        break;
//                    } else if (resultCommand.startsWith("registrationFailed")) {
//                        Platform.runLater(() -> createAlert("Такой логин уже существует" +
//                                " с другим паролем. Введите другую пару логин/пароль для регистрации"));
//                    }
//                } else if (obj.getClass().equals(Integer.class)) {
//                    List<FileInfo> resultCommand = (List<FileInfo>) obj;
//                    Platform.runLater(() -> createServerListFiles(resultCommand));
//                    Platform.runLater(() -> System.out.println("Поступил в контроллер лист " + resultCommand)); // убрать. для проверки
//                }
//            }
//
//
//            //дальше должен бы идти цикл для работы с хранилищем, но пока, вроде, все в одном цикле происходит
////            while (true) {
////                List<FileInfo> resultCommand = (List<FileInfo>) networkService.readCommandResult();
////                Platform.runLater(() -> createServerListFiles(resultCommand));
////                Platform.runLater(() -> System.out.println("Поступил в контроллер лист " + resultCommand)); // убрать. для проверки
////            }
//        }).start();
//    }

    //  в хранилище будут просто файлы. по папкам не будет движения - убрать этот метод и кнопку из fxml
    public void serverMoveUpInFilePath(ActionEvent actionEvent) {
        Path currentPath = Paths.get(serverPathToFile.getText());
        if(currentPath.endsWith(login)){
            return;
        }
        Path upperPath = currentPath.getParent();
        if (upperPath != null) {
            createClientListFiles(upperPath);
        }
    }




}
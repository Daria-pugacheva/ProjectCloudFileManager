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
import javafx.scene.layout.VBox;
import ru.gb.pugacheva.client.factory.Factory;
import ru.gb.pugacheva.client.service.NetworkService;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class Controller implements Initializable {


    public HBox loginPanel, workPanel;
    public TextField loginField,clientPathToFile, serverPathToFile;
    public PasswordField passwordField;
    @FXML
    TableView <FileInfo> clientFiles, serverFiles;
    public Button download,upload;

    private NetworkService networkService;

    public NetworkService getNetworkService() {
        return networkService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        workPanel.setVisible(false);

        networkService = Factory.getNetworkService(); //TODO: возможно, момент подключения лучше перенести на кнопку регистрации?

        TableColumn <FileInfo,String > clientFileTypeColumn = new TableColumn<>("Тип"); //TODO: упаковать в метод + добавить формирование таблицы в части серверной
        clientFileTypeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFileType().getName()));
        clientFileTypeColumn.setPrefWidth(48);

        TableColumn <FileInfo,String > clientFileNameColumn = new TableColumn<>("Имя файла");
        clientFileNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFileName()));
        clientFileNameColumn.setPrefWidth(240);

        TableColumn <FileInfo,Long > clientFileSizeColumn = new TableColumn<>("Размер файла");
        clientFileSizeColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSize()));
        clientFileSizeColumn.setPrefWidth(120);
        clientFileSizeColumn.setCellFactory(column -> {
            return new TableCell<FileInfo,Long>(){
                @Override
                protected void updateItem(Long item, boolean empty) {
                    super.updateItem(item, empty);
                    if(item ==null || empty){
                        setText(null);
                        setStyle("");
                    }else{
                        String text = String.format("%,d bytes",item);
                        if(item==-1L){
                            text = "DIR";
                        }
                        setText(text);
                    }
                }
            };
        });


        clientFiles.getColumns().addAll(clientFileTypeColumn, clientFileNameColumn,clientFileSizeColumn);

        clientFiles.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getClickCount() ==2){
                    Path currentPath = Paths.get(clientPathToFile.getText());
                    Path newPath = currentPath.resolve(clientFiles.getSelectionModel().getSelectedItem().getFileName());
                    if(Files.isDirectory(newPath)){
                        createClientListFiles(newPath);
                    }
                }

            }
        });

        createClientListFiles(Paths.get("C:/"));  // выводим изначально систему от диска С. МОЖЕТ, перенести на авторизацию

        createCommandResultHandler();

    }

    public void createClientListFiles (Path path){ // ЗАВЕСТИ ОТДЕЛЬНЫЙ КЛАСС/Интерфейс, который отвечает за заполнение таблицы
        try {
            clientPathToFile.setText(path.normalize().toAbsolutePath().toString());
            clientFiles.getItems().clear();
            clientFiles.getItems().addAll(Files.list(path).map(FileInfo :: new).collect(Collectors.toList()));
            clientFiles.sort(); // проверить, как получается сортировка
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Не удалось обновить список файлов", ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void createCommandResultHandler(){
        new Thread(() -> {

            byte [] buffer = new byte [1024];
            //в этом цикле по-идее происходит авторизация
            while (true){
                int bytesFromBuffer = networkService.readCommandResult(buffer); //блокирующая
                String resultCommand = new String(buffer,0,bytesFromBuffer);
                if(resultCommand.startsWith("registrationOK") || resultCommand.startsWith("loginOK")) {
                    Platform.runLater(() -> loginPanel.setVisible(false));
                    Platform.runLater(() -> workPanel.setVisible(true));
                }
            }
            //дальше должен бы идти цикл для работы с хранилищем.
        }).start();
    }

    public void shutdown() {
        networkService.closeConnection();
    }

    public void login(ActionEvent actionEvent) {  //отправка на сервер логина и пароля
        networkService.sendCommand("login " + loginField.getText() + " " + passwordField.getText());
        loginField.clear();
        passwordField.clear();
    }


    public void clientMoveUpInFilePath(ActionEvent actionEvent) {
        Path currentPath = Paths.get(clientPathToFile.getText());
        Path upperPath = currentPath.getParent();
        if(upperPath !=null){
            createClientListFiles(upperPath);
        }

    }


    public void serverMoveUpInFilePath(ActionEvent actionEvent) {

    }

    public void download(ActionEvent actionEvent) {

    }

    public void upload(ActionEvent actionEvent) {

    }
}

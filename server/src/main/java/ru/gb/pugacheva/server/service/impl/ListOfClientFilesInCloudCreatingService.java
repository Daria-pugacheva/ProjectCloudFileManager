package ru.gb.pugacheva.server.service.impl;

import ru.gb.pugacheva.common.domain.FileInfo;
import ru.gb.pugacheva.common.domain.PropertiesReciever;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class ListOfClientFilesInCloudCreatingService {

   // private final Path currentPath = Paths.get("C:\\java\\Course_Project_Cloud\\my-cloud-project\\Cloud");
    private final Path currentPath = Paths.get(PropertiesReciever.getProperties("cloudDirectory"));


    public List<FileInfo> createServerFilesList(String login) {

        try {
            Path userDirectory = currentPath.resolve(login);
            List<FileInfo> list = Files.list(userDirectory).map(FileInfo::new).collect(Collectors.toList());
            System.out.println("Сформирован сервисом лист " + list);
            return list;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}

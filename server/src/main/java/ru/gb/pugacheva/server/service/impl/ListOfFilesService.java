package ru.gb.pugacheva.server.service.impl;

import ru.gb.pugacheva.common.domain.FileInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ListOfFilesService {

   // private final File currentPath = new File("C:\\java\\Course_Project_Cloud\\my-cloud-project\\Cloud");

    private final Path currentPath = Paths.get("C:\\java\\Course_Project_Cloud\\my-cloud-project\\Cloud");

    public List<FileInfo> createServerFilesList (String login){
//        Path userDirectory = currentPath.resolve(login);
//        List <FileInfo> fileInfos = new ArrayList<>();
//        fileInfos.add(new FileInfo(userDirectory));
//        return fileInfos;


        try { //TODO: доделать создание листа на слуай, когда папка клиента на сервере пустая
            Path userDirectory = currentPath.resolve(login);
            List <FileInfo> list =  Files.list(userDirectory).map(FileInfo :: new).collect(Collectors.toList());
            System.out.println("Сформирован сервисом лист " + list);
            return list;
        } catch (IOException e) {
            e.printStackTrace();
            return null ;
        }

//        File userDirectory = new File(currentPath, login);
//        List <FileInfo> list =  Arrays.asList(userDirectory.list()).stream().map(item -> new FileInfo(Paths.get(item))).collect(Collectors.toList());
//        System.out.println(list);
//        return list;
    }

}

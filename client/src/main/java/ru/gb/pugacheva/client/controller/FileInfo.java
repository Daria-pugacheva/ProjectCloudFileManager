package ru.gb.pugacheva.client.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileInfo {

    public enum FileType{

        FILE ("F"), DIRECTORY ("D");

        private String name;

        public String getName() {
            return name;
        }

        FileType(String name) {
            this.name = name;
        }
    }

    private String fileName;

    private FileType fileType;

    private long size;

    //РАСШИРЕНИЕ ЕЩЕ МОЖНО ДОБАВИТЬ.


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public FileInfo(Path path) {
        try {
            this.fileName=path.getFileName().toString();
            this.size = Files.size(path);
            this.fileType = Files.isDirectory(path) ? FileType.DIRECTORY : FileType.FILE;
            if(this.fileType == FileType.DIRECTORY){ // ВОЗМОЖНО, ЛИШНЯЯ ИНФО (ПОСМОТРЕТЬ ДАЛЕЕ)
                this.size = -1L;
            }
        } catch (IOException e) {
            throw new RuntimeException("Не удалось собрать информацию о фале по пути " + path);
        }
    }
}

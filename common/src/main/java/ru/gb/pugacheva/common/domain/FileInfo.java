package ru.gb.pugacheva.common.domain;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileInfo implements Serializable {

    public enum FileType implements Serializable {

        FILE("F"), DIRECTORY("D");
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
    private String path;

    public String getFileName() {
        return fileName;
    }

    public FileType getFileType() {
        return fileType;
    }

    public long getSize() {
        return size;
    }

    public FileInfo(Path path) {
        try {
            this.path = path.toString();
            this.fileName = path.getFileName().toString();
            this.size = Files.size(path);
            this.fileType = Files.isDirectory(path) ? FileType.DIRECTORY : FileType.FILE;
            if (this.fileType == FileType.DIRECTORY) {
                this.size = -1L;
            }
        } catch (IOException e) {
            throw new RuntimeException("Не удалось собрать информацию о файле по пути " + path);
        }
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "fileName='" + fileName + '\'' +
                ", fileType=" + fileType +
                ", size=" + size +
                ", path=" + path +
                '}';
    }
}

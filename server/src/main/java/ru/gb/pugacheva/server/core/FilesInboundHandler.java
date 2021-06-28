package ru.gb.pugacheva.server.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import ru.gb.pugacheva.common.domain.Command;
import ru.gb.pugacheva.common.domain.FileInfo;
import ru.gb.pugacheva.server.factory.Factory;
import ru.gb.pugacheva.server.service.CommandDictionaryService;
import ru.gb.pugacheva.server.service.impl.ListOfFilesService;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FilesInboundHandler extends ChannelInboundHandlerAdapter {

 private CommandDictionaryService dictionaryService; //TODO : оптимизировать, чтобы через словарь работало
   private ListOfFilesService listOfFilesService;

    private static String fileName;
    private static String userDirectory;
    private static Long fileSize;
    private static String userLogin;

    public static void setUserLogin(String userLogin) {
        FilesInboundHandler.userLogin = userLogin;
    }

    public static void setFileSize(Long fileSize) {
        FilesInboundHandler.fileSize = fileSize;
    }

    public static String getUserDirectory() {
        return userDirectory;
    }

    public static void setUserDirectory(String userDirectory) {
        FilesInboundHandler.userDirectory = userDirectory;
    }

    public static String getFileName() {
        return fileName;
    }

    public static void setFileName(String fileName) {
        FilesInboundHandler.fileName = fileName;
    }

    public FilesInboundHandler() {
        this.dictionaryService = Factory.getCommandDictionaryService();
        this.listOfFilesService = Factory.getListOfFilesService();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object chunkedFile) throws Exception {
        ByteBuf byteBuf = (ByteBuf) chunkedFile;
        String absoluteFileNameForCloud = userDirectory + fileName;
        File newfile = new File(absoluteFileNameForCloud); //TODO: доделать какой-то алерт/excwption, если файл существует
        newfile.createNewFile();
        System.out.println("10. Должен быть создан файл и Запущен прием фала а сервере по пути " + absoluteFileNameForCloud);

        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(absoluteFileNameForCloud,true))) {
            while (byteBuf.isReadable()) {
                out.write(byteBuf.readByte());
            }
            byteBuf.release();
        }

            if(newfile.length()==fileSize){
                System.out.println("11. Файл должен быть вычитан");
                ctx.pipeline().remove(ChunkedWriteHandler.class);
                String [] args = {fileName};
                ctx.writeAndFlush(new Command("UploadFinished", args));
//                //блок кода по отправке оновленого листа с файлами на клиент - оптимизировать надо. Такой же на CommandInboundHandlere
//                List<FileInfo> resultListOfFiles = listOfFilesService.createServerFilesList(userLogin);
//                System.out.println("На хэндлере лист " + resultListOfFiles);
//                String pathToClientDirectory = userLogin + ":\\";
//                Object [] fileListArgs = new Object[2];
//                fileListArgs[0] = pathToClientDirectory;
//                fileListArgs[1] = resultListOfFiles;
//                Command resultToupgradeFilesList = new Command("cloudFilesList",args);
//                ctx.writeAndFlush(resultToupgradeFilesList);
//                //блок по отрпавке листа файлов закончился
                System.out.println("12. На клиент с сервера отправлена команда UploadFinished");
                ctx.pipeline().addFirst(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                ctx.pipeline().addLast(new CommandInboundHandler());
                ctx.pipeline().remove(FilesInboundHandler.class);
                System.out.println("13. Пайплайн на сервере после отправки файла " + ctx.pipeline().toString());
            }

        }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace(); // потом заменить на логер
    }

    // Входящий будет файл
}

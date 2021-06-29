package ru.gb.pugacheva.server.core;

import io.netty.channel.*;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedWriteHandler;
import ru.gb.pugacheva.common.domain.Command;
import ru.gb.pugacheva.common.domain.FileInfo;
import ru.gb.pugacheva.server.factory.Factory;
import ru.gb.pugacheva.server.service.CommandDictionaryService;
import ru.gb.pugacheva.server.service.impl.ListOfFilesService;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class CommandInboundHandler extends SimpleChannelInboundHandler <Command> {

    CommandDictionaryService dictionaryService;
    ListOfFilesService listOfFilesService;
    private final Path currentPath = Paths.get("C:\\java\\Course_Project_Cloud\\my-cloud-project\\Cloud");

    public CommandInboundHandler() {

        this.dictionaryService = Factory.getCommandDictionaryService();
        this.listOfFilesService = Factory.getListOfFilesService();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command command) throws Exception {
        if(command.getCommandName().startsWith("login")){  // тут, похоже, можно упростить
            String resultOfCommand = (String) dictionaryService.processCommand(command);
            String [] textCommand = resultOfCommand.split("\\s");
            String[] commandArgs = Arrays.copyOfRange(textCommand, 1, textCommand.length);
            Command result = new Command(textCommand[0],commandArgs);
            System.out.println("запущена идентификация клиента");// убрать . для проверки
            System.out.println("отправляю на клиент ответ на регистрацию " + result.getCommandName() + Arrays.toString(result.getArgs()));
            ctx.writeAndFlush(result);
        }else if(command.getCommandName().startsWith("filesList")){
         // List <FileInfo> result = (List <FileInfo>) dictionaryService.processCommand(command);

           System.out.println("пришел запрос на лист файлов"); // убрать. для проверки
           List <FileInfo> resultListOfFiles = listOfFilesService.createServerFilesList((String) command.getArgs()[0]);
           System.out.println("На хэндлере лист " + resultListOfFiles);
           String pathToClientDirectory = (String) command.getArgs()[0] + ":\\";
           Object [] args = new Object[2];
           args[0] = pathToClientDirectory;
           args[1] = resultListOfFiles;
           Command result = new Command("cloudFilesList",args);
           ctx.writeAndFlush(result);
       }else if(command.getCommandName().startsWith("upload")){ // команда с клиента будет uploadFile имя файла??
           System.out.println("2. Сервером на хэндлере получена команда upload" + Arrays.asList(command.getArgs()));
           ctx.pipeline().addLast(new ChunkedWriteHandler());
           ctx.pipeline().addLast(new FilesInboundHandler());
           FilesInboundHandler.setFileName((String) command.getArgs()[0]);
           Path userDirectory = currentPath.resolve((String) command.getArgs()[2]);
           FilesInboundHandler.setUserDirectory(userDirectory.toString() +"\\");
           FilesInboundHandler.setFileSize((Long)command.getArgs()[3]);
           FilesInboundHandler.setUserLogin((String) command.getArgs()[2]);
           ctx.pipeline().remove(ObjectDecoder.class);
           ctx.pipeline().remove(CommandInboundHandler.class);
           System.out.println("3.После смены хэндлеров на сервере они выстроились в последовательность" + ctx.pipeline().toString());
           ctx.writeAndFlush(new Command("readyToUpload", command.getArgs()));
           System.out.println("4.C сервера по идее, отправлена команда readyToUpload" + Arrays.asList(command.getArgs()));
          // ctx.pipeline().remove(ObjectEncoder.class);
       }else  if(command.getCommandName().startsWith("download")){
           System.out.println("2. Сервером на хэндлере получена команда download" + Arrays.asList(command.getArgs()));
            Path pathToFile = currentPath.resolve((String) command.getArgs()[1]).resolve((String) command.getArgs()[0]); //директория юзера
            String absolutePathOfDownloadFile = pathToFile.toString(); // путь к файлу на сервереTODO: смотреть, почему файл не находится, когда проваливаюсь в папки.
            Object [] newArgs = {command.getArgs()[0], absolutePathOfDownloadFile,command.getArgs()[2]}; // имя файал, абсолютный путь на сервере, длинна
            ctx.writeAndFlush(new Command("readyToDownload",newArgs)); // ОТПРАВЛЯЕМ, ПОКА ЕСТЬ В ЦЕПИ ЭНКОДЕР
            System.out.println("4.C сервера по идее, отправлена команда readyToDownload" + Arrays.asList(newArgs));
           ctx.pipeline().remove(ObjectEncoder.class);
           ctx.pipeline().addLast(new ChunkedWriteHandler());
           System.out.println("3.После смены хэндлеров на сервере они выстроились в последовательность" + ctx.pipeline().toString());

       }else if (command.getCommandName().startsWith("readyToRecieve")){
            ChannelFuture future = ctx.channel().writeAndFlush(new ChunkedFile(new File((String) command.getArgs()[1])));
            System.out.println("8. Должна идти передача файла " + command.getArgs()[1]);
            future.addListener((ChannelFutureListener) channelFuture -> System.out.println(" 9. Файл передан"));
            //dictionaryService.processCommand(command);  // тут ничего менять не надо в пайплайне. Ожидаем команду readyToRecieve и аргументы имя файла и абсолютный путь
        }else if (command.getCommandName().startsWith("finishedDownload")){
            ctx.pipeline().remove(ChunkedWriteHandler.class);
            ctx.pipeline().remove(ObjectDecoder.class);
            ctx.pipeline().addFirst(new ObjectEncoder());
            ctx.pipeline().addFirst(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
            System.out.println("Пайплайн на сервере после смены хэндлеров " + ctx.pipeline().toString());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace(); // потом заменить на логер
    }


}

//public class CommandInboundHandler extends ChannelInboundHandlerAdapter {
//
//    CommandDictionaryService dictionaryService;
//
//    public CommandInboundHandler() {
//        this.dictionaryService = Factory.getCommandDictionaryService();
//    }
//
//
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        Command command = (Command) msg;
//        if (command.getCommandName().startsWith("login")) {
//            String result = (String) dictionaryService.processCommand(command);
//            ctx.writeAndFlush(result);
//        }
//        if (command.getCommandName().startsWith("filesList")) {
//            ArrayList<FileInfo> result = (ArrayList<FileInfo>) dictionaryService.processCommand(command);
//            ctx.writeAndFlush(result);
//        }
//    }
//}

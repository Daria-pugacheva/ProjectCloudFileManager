package ru.gb.pugacheva.server.core;

import io.netty.channel.*;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import ru.gb.pugacheva.common.domain.Command;
import ru.gb.pugacheva.common.domain.FileInfo;
import ru.gb.pugacheva.server.factory.Factory;
import ru.gb.pugacheva.server.service.CommandDictionaryService;
import ru.gb.pugacheva.server.service.impl.ListOfFilesService;

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
        }
       if(command.getCommandName().startsWith("filesList")){
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
       }
       if(command.getCommandName().startsWith("upload")){ // команда с клиента будет uploadFile имя файла??
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

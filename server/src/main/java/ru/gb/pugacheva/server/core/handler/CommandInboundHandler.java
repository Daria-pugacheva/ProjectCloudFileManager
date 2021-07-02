package ru.gb.pugacheva.server.core.handler;

import io.netty.channel.*;
import io.netty.handler.stream.ChunkedFile;
import ru.gb.pugacheva.common.domain.Command;
import ru.gb.pugacheva.common.domain.FileInfo;
import ru.gb.pugacheva.common.domain.PropertiesReciever;
import ru.gb.pugacheva.server.core.ServerPipelineCheckoutService;
import ru.gb.pugacheva.server.factory.Factory;
import ru.gb.pugacheva.server.service.CommandDictionaryService;
import ru.gb.pugacheva.server.service.impl.ListOfClientFilesInCloudCreatingService;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class CommandInboundHandler extends SimpleChannelInboundHandler<Command> {

    private CommandDictionaryService dictionaryService;
    private ListOfClientFilesInCloudCreatingService listOfClientFilesInCloudCreatingService; //TODO: временная мера. Скорректировать, чтобы сервис работал через словарь
    //private final Path currentPath = Paths.get("C:\\java\\Course_Project_Cloud\\my-cloud-project\\Cloud");
    private final Path currentPath = Paths.get(PropertiesReciever.getProperties("cloudDirectory"));

    public CommandInboundHandler() {
        this.dictionaryService = Factory.getCommandDictionaryService();
        this.listOfClientFilesInCloudCreatingService = Factory.getListOfFilesService();
    }

    //TODO:смотреть, как упростить код за счет делегирования
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command command) throws Exception {
        if (command.getCommandName().startsWith("login")) {
            ctx.writeAndFlush(createLoginAccept(command));
        } else if (command.getCommandName().startsWith("filesList")) {
            ctx.writeAndFlush(createListOfClientFilesInCloud(command));
        } else if (command.getCommandName().startsWith("upload")) {
            System.out.println("2. Сервером на хэндлере получена команда upload" + Arrays.asList(command.getArgs()));
            setFieldsValueInFilesInboundHandler(command);
            ServerPipelineCheckoutService.createPipelineForInboundFilesRecieving(ctx);
            System.out.println("3.После смены хэндлеров на сервере они выстроились в последовательность" + ctx.pipeline().toString());
            ctx.writeAndFlush(new Command("readyToUpload", command.getArgs()));
            System.out.println("4.C сервера по идее, отправлена команда readyToUpload" + Arrays.asList(command.getArgs()));
        } else if (command.getCommandName().startsWith("download")) {
            ctx.writeAndFlush(createReadytoDownloadAccept(command));
            ServerPipelineCheckoutService.createPipelineForOutboundFilesSending(ctx);
            System.out.println("3.После смены хэндлеров на сервере они выстроились в последовательность" + ctx.pipeline().toString());
        } else if (command.getCommandName().startsWith("readyToRecieve")) {
            ctx.channel().writeAndFlush(new ChunkedFile(new File((String) command.getArgs()[1])));
//            ChannelFuture future = ctx.channel().writeAndFlush(new ChunkedFile(new File((String) command.getArgs()[1])));
//            System.out.println("8. Должна идти передача файла " + command.getArgs()[1]);
//            future.addListener((ChannelFutureListener) channelFuture -> System.out.println(" 9. Файл передан"));
        } else if (command.getCommandName().startsWith("finishedDownload")) {
            ServerPipelineCheckoutService.createBasePipelineAfterDownloadForInOutCommandTraffic(ctx);
            System.out.println("Пайплайн на сервере после смены хэндлеров " + ctx.pipeline().toString());
        }
    }

    private Command createLoginAccept (Command command){
        String resultOfCommand = (String) dictionaryService.processCommand(command);
        String[] textCommand = resultOfCommand.split("\\s");
        String[] commandArgs = Arrays.copyOfRange(textCommand, 1, textCommand.length);
        Command result = new Command(textCommand[0], commandArgs);
        System.out.println("запущена идентификация клиента");
        System.out.println("отправляю на клиент ответ на регистрацию " + result.getCommandName() + Arrays.toString(result.getArgs()));
        return result;
    }

    private Command createListOfClientFilesInCloud (Command command){
        //System.out.println("пришел запрос на лист файлов");
        List<FileInfo> resultListOfFiles = listOfClientFilesInCloudCreatingService.createServerFilesList((String) command.getArgs()[0]);
       // System.out.println("На хэндлере лист " + resultListOfFiles);
        String pathToClientDirectory = command.getArgs()[0] + ":\\";
        Object[] args = new Object[2];
        args[0] = pathToClientDirectory;
        args[1] = resultListOfFiles;
        Command result = new Command("cloudFilesList", args);
        return result;
    }

    private void setFieldsValueInFilesInboundHandler (Command command){
        FilesInboundHandler.setFileName((String) command.getArgs()[0]);
        Path userDirectory = currentPath.resolve((String) command.getArgs()[2]);
        FilesInboundHandler.setUserDirectory(userDirectory.toString() + "\\");
        FilesInboundHandler.setFileSize((Long) command.getArgs()[3]);
        FilesInboundHandler.setLogin((String) command.getArgs()[2]);

    }

    private Command createReadytoDownloadAccept (Command command){
        System.out.println("2. Сервером на хэндлере получена команда download" + Arrays.asList(command.getArgs()));
        Path pathToFile = currentPath.resolve((String) command.getArgs()[1]).resolve((String) command.getArgs()[0]);
        String absolutePathOfDownloadFile = pathToFile.toString();
        Object[] newArgs = {command.getArgs()[0], absolutePathOfDownloadFile, command.getArgs()[2],command.getArgs()[3]};
        Command result = new Command("readyToDownload", newArgs);
        System.out.println("4.C сервера по идее, отправлена команда readyToDownload" + Arrays.asList(newArgs));
        return result;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }

}


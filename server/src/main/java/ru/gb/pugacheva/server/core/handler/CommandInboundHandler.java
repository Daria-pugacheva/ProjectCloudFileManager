package ru.gb.pugacheva.server.core.handler;

import io.netty.channel.*;
import io.netty.handler.stream.ChunkedFile;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.gb.pugacheva.common.domain.Command;
import ru.gb.pugacheva.common.domain.CommandType;
import ru.gb.pugacheva.common.domain.FileInfo;
import ru.gb.pugacheva.common.domain.PropertiesReciever;
import ru.gb.pugacheva.server.core.ServerPipelineCheckoutService;
import ru.gb.pugacheva.server.factory.Factory;
import ru.gb.pugacheva.server.service.CommandDictionaryService;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class CommandInboundHandler extends SimpleChannelInboundHandler<Command> {

    private final CommandDictionaryService dictionaryService;
    private final Path currentPath = Paths.get(PropertiesReciever.getProperties("cloudDirectory"));
    private static final Logger LOGGER = LogManager.getLogger(CommandInboundHandler.class);

    public CommandInboundHandler() {
        this.dictionaryService = Factory.getCommandDictionaryService();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command command) throws Exception {
        if (command.getCommandName().startsWith(CommandType.LOGIN.toString())) {
            ctx.writeAndFlush(createLoginAccept(command));
        } else if (command.getCommandName().startsWith(CommandType.FILESLIST.toString())) {
            ctx.writeAndFlush(createListOfClientFilesInCloud(command));
        } else if (command.getCommandName().startsWith(CommandType.UPLOAD.toString())) {
            LOGGER.info("От клиента на сервере получена команда UPLOAD с аргументами " + Arrays.asList(command.getArgs()));
            setFieldsValueInFilesInboundHandler(command);
            ServerPipelineCheckoutService.createPipelineForInboundFilesRecieving(ctx);

            ctx.writeAndFlush(new Command(CommandType.READY_TO_UPLOAD.toString(), command.getArgs()));
            LOGGER.info("C сервера клиенту отправлена команда READY_TO_UPLOAD с аргументами " + Arrays.asList(command.getArgs()));
        } else if (command.getCommandName().startsWith(CommandType.DOWNLOAD.toString())) {
            ctx.writeAndFlush(createReadytoDownloadAccept(command));
            ServerPipelineCheckoutService.createPipelineForOutboundFilesSending(ctx);
        } else if (command.getCommandName().startsWith(CommandType.READY_TO_RECIEVE.toString())) {
            ChannelFuture future = ctx.channel().writeAndFlush(new ChunkedFile(new File((String) command.getArgs()[1])));
            LOGGER.info("Началась с сервера на клиента передача файла: " + command.getArgs()[1]);
            future.addListener((ChannelFutureListener) channelFuture -> LOGGER.info("Файл передан"));
        } else if (command.getCommandName().startsWith(CommandType.FINISHED_DOWNLOAD.toString())) {
            ServerPipelineCheckoutService.createBasePipelineAfterDownloadForInOutCommandTraffic(ctx);
        }
    }

    private Command createLoginAccept(Command command) {
        String resultOfCommand = (String) dictionaryService.processCommand(command);
        String[] textCommand = resultOfCommand.split("\\s");
        String[] commandArgs = Arrays.copyOfRange(textCommand, 1, textCommand.length);
        Command result = new Command(textCommand[0], commandArgs);
        LOGGER.info("Клиенту отправляется ответ на запрос авторизации " + result.getCommandName() + Arrays.toString(result.getArgs()));
        return result;
    }

    private Command createListOfClientFilesInCloud(Command command) {
        LOGGER.info("От клиента на сервер поступила команда FILESLIST (запрос на отправку списка файлов в облаке");
        List<FileInfo> resultListOfFiles = (List<FileInfo>) dictionaryService.processCommand(command);
        String pathToClientDirectory = command.getArgs()[0] + ":\\";
        Object[] args = new Object[2];
        args[0] = pathToClientDirectory;
        args[1] = resultListOfFiles;
        Command result = new Command(CommandType.CLOUD_FILESLIST.toString(), args);
        return result;
    }

    private void setFieldsValueInFilesInboundHandler(Command command) {
        FilesInboundHandler.setFileName((String) command.getArgs()[0]);
        Path userDirectory = currentPath.resolve((String) command.getArgs()[2]);
        FilesInboundHandler.setUserDirectory(userDirectory.toString() + "\\");
        FilesInboundHandler.setFileSize((Long) command.getArgs()[3]);
        FilesInboundHandler.setLogin((String) command.getArgs()[2]);

    }

    private Command createReadytoDownloadAccept(Command command) {
        LOGGER.info("Сервером от клиента получена команда DOWNLOAD с аргументами " + Arrays.asList(command.getArgs()));
        Path pathToFile = currentPath.resolve((String) command.getArgs()[1]).resolve((String) command.getArgs()[0]);
        String absolutePathOfDownloadFile = pathToFile.toString();
        Object[] newArgs = {command.getArgs()[0], absolutePathOfDownloadFile, command.getArgs()[2], command.getArgs()[3]};
        Command result = new Command(CommandType.READY_TO_DOWNLOAD.toString(), newArgs);
        LOGGER.info("C сервера на клиент отправлена команда READY_TO_DOWNLOAD с аргументами" + Arrays.asList(newArgs));
        return result;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.throwing(Level.ERROR, cause);
    }

}


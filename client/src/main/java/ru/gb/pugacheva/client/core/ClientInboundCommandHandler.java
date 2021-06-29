package ru.gb.pugacheva.client.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import ru.gb.pugacheva.client.MainClientApp;
import ru.gb.pugacheva.client.controller.Controller;
import ru.gb.pugacheva.client.factory.Factory;
import ru.gb.pugacheva.client.service.CommandDictionaryService;
import ru.gb.pugacheva.common.domain.Command;

import java.util.Arrays;

public class ClientInboundCommandHandler extends SimpleChannelInboundHandler<Command> {
    //TODO: 1. внимательно логировать, убрать все лишние sout; 2.смотреть, может, как-то декомпозировать код
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command command){
        if (command.getCommandName().startsWith("readyToUpload")) {
            System.out.println("5.На клиенте получена команда readyToUpload" + Arrays.asList(command.getArgs()));
            ctx.pipeline().addLast(new ChunkedWriteHandler());
            ctx.pipeline().remove(ObjectEncoder.class);
            System.out.println("6. Пайплайна на клиенте после смены " + ctx.pipeline().toString());
            CommandDictionaryService commandDictionary = Factory.getCommandDictionary();
            commandDictionary.processCommand(command);
        } else if (command.getCommandName().startsWith("UploadFinished")) {
            System.out.println("13 Получена с сервера команда UploadFinished для файла " + Arrays.asList(command.getArgs()));
            ctx.pipeline().remove(ChunkedWriteHandler.class);
            ctx.pipeline().remove(ObjectDecoder.class);
            ctx.pipeline().addFirst(new ObjectEncoder());
            ctx.pipeline().addFirst(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
            System.out.println("Пайплайн на клиенте после обратной смены " + ctx.pipeline().toString());
            Controller currentController = (Controller) MainClientApp.getActiveController();
            String[] args = {currentController.getLogin()};
            currentController.getNetworkService().sendCommand(new Command("filesList", args));
            currentController.uploadButton.setDisable(false);
            currentController.downloadButton.setDisable(false);
        } else if (command.getCommandName().startsWith("readyToDownload")) {
            System.out.println("5.На клиенте получена команда readyToDownload" + Arrays.asList(command.getArgs()));
            FilesInboundClientHandler.setFileName((String) command.getArgs()[0]);
            FilesInboundClientHandler.setFileSize((Long) command.getArgs()[2]);
            Controller currentController = (Controller) MainClientApp.getActiveController();
            String userDirectoryForDounload = currentController.clientPathToFile.getText();
            FilesInboundClientHandler.setUserDirectory(userDirectoryForDounload);
            ctx.pipeline().remove(ClientInboundCommandHandler.class);
            ctx.pipeline().remove(ObjectDecoder.class);
            ctx.pipeline().addLast(new ChunkedWriteHandler());
            ctx.pipeline().addLast(new FilesInboundClientHandler());
            System.out.println("6. Пайплайна на клиенте после смены " + ctx.pipeline().toString());
            Object[] argsToServer = {command.getArgs()[0], command.getArgs()[1]};
            ctx.writeAndFlush(new Command("readyToRecieve", argsToServer));
            System.out.println("7. На сервер отправлена каманда readyToRecieve " + Arrays.asList(argsToServer));
        } else {
            CommandDictionaryService commandDictionary = Factory.getCommandDictionary();
            commandDictionary.processCommand(command);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println(cause);
    }
}





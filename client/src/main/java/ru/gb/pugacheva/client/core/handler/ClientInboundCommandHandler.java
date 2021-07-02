package ru.gb.pugacheva.client.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import ru.gb.pugacheva.client.core.ClientPipelineCheckoutService;
import ru.gb.pugacheva.client.factory.Factory;
import ru.gb.pugacheva.client.service.Callback;
import ru.gb.pugacheva.client.service.CommandDictionaryService;
import ru.gb.pugacheva.common.domain.Command;

import java.util.Arrays;

public class ClientInboundCommandHandler extends SimpleChannelInboundHandler<Command> {

    private Callback setButtonsAbleAndUpdateFilesLIstCallback;

    public ClientInboundCommandHandler(Callback setButtonsAbleAndUpdateFilesLIstCallback) {
        this.setButtonsAbleAndUpdateFilesLIstCallback = setButtonsAbleAndUpdateFilesLIstCallback;
    }

    //TODO: 1. внимательно логировать, убрать все лишние sout; 2.смотреть, может, как-то декомпозировать код
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command command){
        if (command.getCommandName().startsWith("readyToUpload")) {
            System.out.println("5.На клиенте получена команда readyToUpload" + Arrays.asList(command.getArgs()));
            ClientPipelineCheckoutService.createPipelineForFilesSending(ctx);
            System.out.println("6. Пайплайна на клиенте после смены " + ctx.pipeline().toString());
            CommandDictionaryService commandDictionary = Factory.getCommandDictionary();
            commandDictionary.processCommand(command);
        } else if (command.getCommandName().startsWith("UploadFinished")) {
            System.out.println("13 Получена с сервера команда UploadFinished для файла " + Arrays.asList(command.getArgs()));
            ClientPipelineCheckoutService.createBasePipelineAfterUploadForInOutCommandTraffic(ctx);
            System.out.println("Пайплайн на клиенте после обратной смены " + ctx.pipeline().toString());

            String[] args = {(String) command.getArgs()[1]};
            ctx.writeAndFlush(new Command("filesList", args));

            if(setButtonsAbleAndUpdateFilesLIstCallback!=null){
                setButtonsAbleAndUpdateFilesLIstCallback.callback();
            }

        } else if (command.getCommandName().startsWith("readyToDownload")) {
            System.out.println("5.На клиенте получена команда readyToDownload" + Arrays.asList(command.getArgs()));

            setFieldsValueInFilesInboundClientHandler(command);
            ClientPipelineCheckoutService.createPipelineForInboundFilesRecieving(ctx);
            System.out.println("6. Пайплайна на клиенте после смены " + ctx.pipeline().toString());
            Object[] argsToServer = {command.getArgs()[0], command.getArgs()[1]};
            ctx.writeAndFlush(new Command("readyToRecieve", argsToServer));
            System.out.println("7. На сервер отправлена каманда readyToRecieve " + Arrays.asList(argsToServer));
        } else {
            CommandDictionaryService commandDictionary = Factory.getCommandDictionary();
            commandDictionary.processCommand(command);
        }
    }

    private void setFieldsValueInFilesInboundClientHandler (Command command){
        FilesInboundClientHandler.setFileName((String) command.getArgs()[0]);
        FilesInboundClientHandler.setFileSize((Long) command.getArgs()[2]);
        FilesInboundClientHandler.setUserDirectory((String) command.getArgs()[3]);
        FilesInboundClientHandler.setSetButtonsAbleCallback(setButtonsAbleAndUpdateFilesLIstCallback);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println(cause);
    }

}







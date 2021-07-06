package ru.gb.pugacheva.client.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.gb.pugacheva.client.core.ClientPipelineCheckoutService;
import ru.gb.pugacheva.client.factory.Factory;
import ru.gb.pugacheva.client.service.Callback;
import ru.gb.pugacheva.client.service.CommandDictionaryService;
import ru.gb.pugacheva.common.domain.Command;
import ru.gb.pugacheva.common.domain.CommandType;

import java.util.Arrays;

public class ClientInboundCommandHandler extends SimpleChannelInboundHandler<Command> {

    private Callback setButtonsAbleAndUpdateFilesLIstCallback;
    private static final Logger LOGGER = LogManager.getLogger(ClientInboundCommandHandler.class);

    public ClientInboundCommandHandler(Callback setButtonsAbleAndUpdateFilesLIstCallback) {
        this.setButtonsAbleAndUpdateFilesLIstCallback = setButtonsAbleAndUpdateFilesLIstCallback;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command command) {
        if (command.getCommandName().startsWith(CommandType.READY_TO_UPLOAD.toString())) {
            LOGGER.info("Получена с сервера команда READY_TO_UPLOAD со списком аргументов: " + Arrays.asList(command.getArgs()));
            ClientPipelineCheckoutService.createPipelineForFilesSending(ctx);

            CommandDictionaryService commandDictionary = Factory.getCommandDictionary();
            commandDictionary.processCommand(command);
        } else if (command.getCommandName().startsWith(CommandType.UPLOAD_FINISHED.toString())) {
            LOGGER.info("Получена с сервера команда UPLOAD_FINISHED для файла " + Arrays.asList(command.getArgs()));
            ClientPipelineCheckoutService.createBasePipelineAfterUploadForInOutCommandTraffic(ctx);

            String[] args = {(String) command.getArgs()[1]};
            ctx.writeAndFlush(new Command(CommandType.FILESLIST.toString(), args));
            LOGGER.info("На сервер отправлена команда FILESLIST с аргументами " + args);

            if (setButtonsAbleAndUpdateFilesLIstCallback != null) {
                setButtonsAbleAndUpdateFilesLIstCallback.callback();
            }

        } else if (command.getCommandName().startsWith(CommandType.READY_TO_DOWNLOAD.toString())) {
            LOGGER.info("Получена с сервера команда READY_TO_DOWNLOAD с аргументами " + Arrays.asList(command.getArgs()));

            ClientPipelineCheckoutService.createPipelineForInboundFilesRecieving(ctx, (String) command.getArgs()[0],
                    (String) command.getArgs()[3], (Long) command.getArgs()[2], setButtonsAbleAndUpdateFilesLIstCallback);

            Object[] argsToServer = {command.getArgs()[0], command.getArgs()[1]};
            ctx.writeAndFlush(new Command(CommandType.READY_TO_RECIEVE.toString(), argsToServer));
            LOGGER.info("На сервер отправлена каманда READY_TO_RECIEVE с аргументами " + Arrays.asList(argsToServer));
        } else {
            CommandDictionaryService commandDictionary = Factory.getCommandDictionary();
            commandDictionary.processCommand(command);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.throwing(Level.ERROR, cause);
    }

}







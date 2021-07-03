package ru.gb.pugacheva.server.core.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.gb.pugacheva.common.domain.Command;
import ru.gb.pugacheva.common.domain.CommandType;
import ru.gb.pugacheva.server.core.ServerPipelineCheckoutService;
import ru.gb.pugacheva.server.factory.Factory;
import ru.gb.pugacheva.server.service.CommandDictionaryService;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class FilesInboundHandler extends ChannelInboundHandlerAdapter {

    private static String fileName;
    private static String userDirectory;
    private static Long fileSize;
    private static String login;

    private static final Logger LOGGER = LogManager.getLogger(FilesInboundHandler.class);

    public static void setFileSize(Long fileSize) {
        FilesInboundHandler.fileSize = fileSize;
    }
    public static void setUserDirectory(String userDirectory) {
        FilesInboundHandler.userDirectory = userDirectory;
    }
    public static void setFileName(String fileName) {
        FilesInboundHandler.fileName = fileName;
    }
    public static void setLogin(String login) {
        FilesInboundHandler.login = login;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object chunkedFile) throws Exception {
        ByteBuf byteBuf = (ByteBuf) chunkedFile;
        String absoluteFileNameForCloud = userDirectory + "\\" + fileName;
        File newfile = new File(absoluteFileNameForCloud);
        newfile.createNewFile();
        LOGGER.info("Создан файл и запущен прием фала а сервере по пути " + absoluteFileNameForCloud);

        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(absoluteFileNameForCloud, true))) {
            while (byteBuf.isReadable()) {
                out.write(byteBuf.readByte());
            }
            byteBuf.release();
        }

        if (newfile.length() == fileSize) {
            LOGGER.info("Файл вычитан");
            ServerPipelineCheckoutService.createBasePipelineAfterUploadForInOutCommandTraffic(ctx);

            String[] args = {fileName, login};
            ctx.writeAndFlush(new Command(CommandType.UPLOAD_FINISHED.toString(), args));
            LOGGER.info("На клиент с сервера отправлена команда UPLOAD_FINISHED с аргументами " + args);

        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.throwing(Level.ERROR, cause);
    }

}

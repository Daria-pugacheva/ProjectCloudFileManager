package ru.gb.pugacheva.client.core.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.gb.pugacheva.client.core.ClientPipelineCheckoutService;
import ru.gb.pugacheva.client.factory.Factory;
import ru.gb.pugacheva.client.service.Callback;
import ru.gb.pugacheva.client.service.CommandDictionaryService;
import ru.gb.pugacheva.client.service.impl.ClientCommandDictionaryServiceImpl;
import ru.gb.pugacheva.common.domain.Command;
import ru.gb.pugacheva.common.domain.CommandType;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class FilesInboundClientHandler extends ChannelInboundHandlerAdapter {

    private static String fileName;
    private static String userDirectory;
    private static Long fileSize;
    private static Callback setButtonsAbleAndUpdateFilesLIstCallback;


    private static final Logger LOGGER = LogManager.getLogger(FilesInboundClientHandler.class);

    public static void setFileSize(Long fileSize) {
        FilesInboundClientHandler.fileSize = fileSize;
    }

    public static void setUserDirectory(String userDirectory) {
        FilesInboundClientHandler.userDirectory = userDirectory;
    }

    public static void setFileName(String fileName) {
        FilesInboundClientHandler.fileName = fileName;
    }

    public static void setSetButtonsAbleCallback(Callback setButtonsAbleAndUpdateFilesLIstCallback) {
        FilesInboundClientHandler.setButtonsAbleAndUpdateFilesLIstCallback = setButtonsAbleAndUpdateFilesLIstCallback;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object chunkedFile) throws Exception {
        ByteBuf byteBuf = (ByteBuf) chunkedFile;
        String absoluteFileNameForClient = userDirectory + "\\" + fileName;
        File newfile = new File(absoluteFileNameForClient);
        newfile.createNewFile();

            LOGGER.info("Создан файл и запущен процесс приема файла на клиенте по пути " + absoluteFileNameForClient);

            try (OutputStream out = new BufferedOutputStream(new FileOutputStream(absoluteFileNameForClient, true))) {
                while (byteBuf.isReadable()) {
                    out.write(byteBuf.readByte());
                }
                byteBuf.release();
            }

            if (newfile.length() == fileSize) {
                LOGGER.info("Файл вычитан");
                ClientPipelineCheckoutService.createBasePipelineAfterDownloadForInOutCommandTraffic(ctx, setButtonsAbleAndUpdateFilesLIstCallback);

                String[] args = {fileName};
                ctx.writeAndFlush(new Command(CommandType.FINISHED_DOWNLOAD.toString(), args));
                LOGGER.info("На сервер с клиента отправлена команда FINISHED_DOWNLOAD с аргументами " + Arrays.asList(args));

                setButtonsAbleAndUpdateFilesLIstCallback.callback(); //TODO: иногда не обновляется лист файлов после загрузки. Проверить, почему
            }
        }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.throwing(Level.ERROR, cause);
    }

}

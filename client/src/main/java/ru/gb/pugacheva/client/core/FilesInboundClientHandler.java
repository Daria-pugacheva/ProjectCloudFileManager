package ru.gb.pugacheva.client.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import ru.gb.pugacheva.client.MainClientApp;
import ru.gb.pugacheva.client.controller.Controller;
import ru.gb.pugacheva.client.factory.Factory;
import ru.gb.pugacheva.client.service.CommandDictionaryService;
import ru.gb.pugacheva.common.domain.Command;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.Arrays;

public class FilesInboundClientHandler extends ChannelInboundHandlerAdapter {

    private CommandDictionaryService dictionaryService; //TODO: или оптимизировать код, чтобы работать через команды, или, если будет удобнее текущаяя реализация, убрать лишнее поле и конструктор
    private Controller currentController;

    private static String fileName;
    private static String userDirectory;
    private static Long fileSize;

    public static void setFileSize(Long fileSize) {
        FilesInboundClientHandler.fileSize = fileSize;
    }

    public static void setUserDirectory(String userDirectory) {
        FilesInboundClientHandler.userDirectory = userDirectory;
    }

    public static void setFileName(String fileName) {
        FilesInboundClientHandler.fileName = fileName;
    }

    public FilesInboundClientHandler() {
        this.dictionaryService = Factory.getCommandDictionary();
        this.currentController = (Controller) MainClientApp.getActiveController();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object chunkedFile) throws Exception {
        ByteBuf byteBuf = (ByteBuf) chunkedFile;
        String absoluteFileNameForClient = userDirectory + "\\" + fileName;
        File newfile = new File(absoluteFileNameForClient); //TODO: доделать какой-то алерт/excеption, если файл не созрать (существует или еще какой косяк)
        newfile.createNewFile();
        System.out.println("9. Должен быть создан файл и идет процесс приема файла на клиенте по пути " + absoluteFileNameForClient);

        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(absoluteFileNameForClient, true))) {
            while (byteBuf.isReadable()) {
                out.write(byteBuf.readByte());
            }
            byteBuf.release();
        }

        if (newfile.length() == fileSize) {
            System.out.println("10. Файл должен быть вычитан");
            ctx.pipeline().remove(ChunkedWriteHandler.class);
            String[] args = {fileName};
            ctx.writeAndFlush(new Command("finishedDownload", args));

            System.out.println("11. На сервер с клиента отправлена команда downloadFinished" + Arrays.asList(args));
            ctx.pipeline().addFirst(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
            ctx.pipeline().addLast(new ClientInboundCommandHandler());
            ctx.pipeline().remove(FilesInboundClientHandler.class);
            System.out.println("12. Пайплайн на клиенте после отправки файла " + ctx.pipeline().toString());

           // Controller currentController = (Controller) MainClientApp.getActiveController();
            currentController.createClientListFiles(Paths.get(userDirectory)); //TODO: иногда не обновляется лист файлов после загрузки. Проверить, почему
            System.out.println("должен обновиться лист файлов в  директории юзера " + userDirectory);
            currentController.downloadButton.setDisable(false);
            currentController.uploadButton.setDisable(false);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }

}

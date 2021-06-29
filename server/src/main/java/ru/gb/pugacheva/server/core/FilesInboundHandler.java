package ru.gb.pugacheva.server.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import ru.gb.pugacheva.common.domain.Command;
import ru.gb.pugacheva.server.factory.Factory;
import ru.gb.pugacheva.server.service.CommandDictionaryService;
import ru.gb.pugacheva.server.service.impl.ListOfFilesService;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class FilesInboundHandler extends ChannelInboundHandlerAdapter {

    private CommandDictionaryService dictionaryService; //TODO : оптимизировать, чтобы через словарь работало

    private static String fileName;
    private static String userDirectory;
    private static Long fileSize;

    public static void setFileSize(Long fileSize) {
        FilesInboundHandler.fileSize = fileSize;
    }

    public static void setUserDirectory(String userDirectory) {
        FilesInboundHandler.userDirectory = userDirectory;
    }

    public static void setFileName(String fileName) {
        FilesInboundHandler.fileName = fileName;
    }

    public FilesInboundHandler() {
        this.dictionaryService = Factory.getCommandDictionaryService();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object chunkedFile) throws Exception {
        ByteBuf byteBuf = (ByteBuf) chunkedFile;
        String absoluteFileNameForCloud = userDirectory + "\\" + fileName;
        File newfile = new File(absoluteFileNameForCloud); //TODO: доделать какой-то алерт/exception, если файл не создать (существует или еще какой косяк)
        newfile.createNewFile();
        System.out.println("10. Должен быть создан файл и Запущен прием фала а сервере по пути " + absoluteFileNameForCloud);

        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(absoluteFileNameForCloud, true))) {
            while (byteBuf.isReadable()) {
                out.write(byteBuf.readByte());
            }
            byteBuf.release();
        }

        if (newfile.length() == fileSize) {
            System.out.println("11. Файл должен быть вычитан");
            ctx.pipeline().remove(ChunkedWriteHandler.class);
            String[] args = {fileName};
            ctx.writeAndFlush(new Command("UploadFinished", args));
            System.out.println("12. На клиент с сервера отправлена команда UploadFinished");
            ctx.pipeline().addFirst(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
            ctx.pipeline().addLast(new CommandInboundHandler());
            ctx.pipeline().remove(FilesInboundHandler.class);
            System.out.println("13. Пайплайн на сервере после отправки файла " + ctx.pipeline().toString());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }

}

package ru.gb.pugacheva.client.core;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.stream.ChunkedFile;

import ru.gb.pugacheva.client.core.handler.ClientInboundCommandHandler;
import ru.gb.pugacheva.client.service.Callback;
import ru.gb.pugacheva.common.domain.Command;
import ru.gb.pugacheva.common.domain.PropertiesReciever;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

public class NettyNetworkService implements NetworkService {

    private static SocketChannel channel;
    private static NettyNetworkService network;

    private static final String SERVER_HOST = PropertiesReciever.getProperties("host");
    private static final int SERVER_PORT = Integer.parseInt(PropertiesReciever.getProperties("port").trim());

    private NettyNetworkService() {
    }

    public static NettyNetworkService initializeNetwork(Callback setButtonsAbleAndUpdateFilesLIstCallback) {
        network = new NettyNetworkService();
        initializeNetworkService(setButtonsAbleAndUpdateFilesLIstCallback);
        return network;
    }

    public static NettyNetworkService getNetwork() { //DEN
        return network;
    }

    private static void initializeNetworkService(Callback setButtonsAbleAndUpdateFilesLIstCallback) {
        Thread t = new Thread(() -> {
            EventLoopGroup workGroup = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(workGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) {
                                channel = socketChannel;
                                socketChannel.pipeline()
                                        .addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)))
                                        .addLast(new ObjectEncoder())
                                        .addLast(new ClientInboundCommandHandler(setButtonsAbleAndUpdateFilesLIstCallback));
                            }
                        });
                ChannelFuture future = bootstrap.connect(SERVER_HOST, SERVER_PORT).sync();
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                workGroup.shutdownGracefully();
            }
        });
        t.setDaemon(true);
        t.start();
    }

    @Override
    public void sendCommand(Command command) {
        System.out.println("command from client is " + command.getCommandName() + Arrays.asList(command.getArgs()));
        channel.writeAndFlush(command);
    }

    @Override
    public void sendFile(String pathToFile) {
        try {
            ChannelFuture future = channel.writeAndFlush(new ChunkedFile(new File(pathToFile))); //TODO: прикинуть, нужен ли листнер (пока удобно для проверки)
            System.out.println("8. Должна стартовать передача файла " + pathToFile);
            future.addListener((ChannelFutureListener) channelFuture -> System.out.println(" 9. Файл передан"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeConnection() {
        try {
            if (isConnected()) {
                channel.close().sync();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isConnected() { // TODO добавить метод в интерфейс
        return channel != null && !channel.isShutdown();
    }

}


package ru.gb.pugacheva.client.service.impl;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.stream.ChunkedFile;

import ru.gb.pugacheva.client.core.ClientInboundCommandHandler;
import ru.gb.pugacheva.client.service.NetworkService;
import ru.gb.pugacheva.common.domain.Command;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class NettyNetworkService implements NetworkService {

    private static SocketChannel channel;
    private static NettyNetworkService network;

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8189;

    private NettyNetworkService() {
    }

    public static NettyNetworkService initializeNetwork() {
        network = new NettyNetworkService();
        initializeNetworkService();
        return network;
    }

    public static NettyNetworkService getNetwork() { //DEN
        return network;
    }

    private static void initializeNetworkService() {
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
                                        .addLast(new ClientInboundCommandHandler());
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


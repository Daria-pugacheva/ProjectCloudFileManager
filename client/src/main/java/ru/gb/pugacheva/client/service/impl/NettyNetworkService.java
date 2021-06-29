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
import ru.gb.pugacheva.client.core.ClientCommandHandler;
import ru.gb.pugacheva.client.service.NetworkService;
import ru.gb.pugacheva.common.domain.Command;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class NettyNetworkService implements NetworkService {  // ПОКА НЕПРИМЕНЯЕМЫЙ КОД (ПОКА ИСПОЛЬЗУЮ ВАРИАНТ IO)

    private static SocketChannel channel;//TODO: для закрытия надо закрыть канал (см.ниже)
    private static NettyNetworkService network; // ОТ ДЕНИСА

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8189;

//   // @Override
//    public static SocketChannel getChannel() {
//        return channel;
//    }

    private NettyNetworkService() { // DEN
    }

    public static NettyNetworkService initializeNetwork() { //DEN
        network = new NettyNetworkService();
        initializeNetworkService();
        return network;
    }

    public static NettyNetworkService getNetwork() { //DEN
        return network;
    }

    //public NettyNetworkService(){ // MY
    private static void initializeNetworkService() {
        //new Thread(()->{ //MY
        Thread t = new Thread(() -> { //DEN
            EventLoopGroup workGroup = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(workGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                channel = socketChannel;
                                socketChannel.pipeline()
                                        .addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null))) //добавили входящий и исходящий хэндлер
                                        .addLast(new ObjectEncoder())
                                        .addLast(new ClientCommandHandler()); //DEN
                            }
                        });
                ChannelFuture future = bootstrap.connect(SERVER_HOST, SERVER_PORT).sync();
                future.channel().closeFuture().sync(); // блокирующая операция //TODO: для закрытия надо закрыть канал (см.выше)
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                workGroup.shutdownGracefully();
            }
        }); // у меня был start тут
        t.setDaemon(true);
        t.start();
    }

    @Override
    public void sendCommand(Command command) {
        System.out.println("command from client is " + command.getCommandName() + Arrays.asList(command.getArgs())); //для проверки
        channel.writeAndFlush(command);
    }
    
    @Override
    public void sendFile(String pathToFile){
        try {
            ChannelFuture future = channel.writeAndFlush(new ChunkedFile(new File(pathToFile)));
            System.out.println("8. Должна стартовать передача файла " + pathToFile);
            future.addListener((ChannelFutureListener) channelFuture -> System.out.println(" 9. Файл передан")); //может, еще куда передадим?
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Object readCommandResult() {
        return null;
    } // DEN - типа при хэндлерах она нам не нужна

    @Override
    public void closeConnection() {
//        if(channel.isOpen()) { // мой вариант условия
//        channel.close();
//    }
        try {  //чуть по-другому вариант Дениса
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


package ru.gb.pugacheva.client.service.impl;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import ru.gb.pugacheva.client.core.ClientHandler;
import ru.gb.pugacheva.client.service.NetworkService;
import ru.gb.pugacheva.common.domain.Command;

public class NettyNetworkService implements NetworkService {  // ПОКА НЕПРИМЕНЯЕМЫЙ КОД (ПОКА ИСПОЛЬЗУЮ ВАРИАНТ IO)

    private SocketChannel channel;//TODO: для закрытия надо закрыть канал (см.ниже)

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8189;


    public NettyNetworkService(){
        new Thread(()->{
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
                                        .addLast(new ObjectEncoder());
                                        //.addLast(new ClientHandler());
                            }
                        });
                ChannelFuture future = bootstrap.connect(SERVER_HOST,SERVER_PORT).sync();
                future.channel().closeFuture().sync(); // блокирующая операция //TODO: для закрытия надо закрыть канал (см.выше)
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                workGroup.shutdownGracefully();
            }
        }).start();
    }

    @Override
    public void sendCommand(Command command) {
        channel.writeAndFlush(command);

    }

    @Override
    public Object readCommandResult() {
        return channel.read();
    }

    @Override
    public void closeConnection() {
        if(channel.isOpen()) {
            channel.close();
        }// так просто ведь?
    }
}

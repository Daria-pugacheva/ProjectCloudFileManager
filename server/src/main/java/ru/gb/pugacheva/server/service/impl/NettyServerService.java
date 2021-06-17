package ru.gb.pugacheva.server.service.impl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import ru.gb.pugacheva.server.core.CommandHandler;
import ru.gb.pugacheva.server.service.ServerService;

public class NettyServerService implements ServerService {

    private static final int SERVER_PORT = 8189;

    @Override
    public void startServer() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1); //поток для подключения клиентов
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline()
                                    .addLast(new StringDecoder()) // переводит из байтв в строки
                                    .addLast(new StringEncoder()) // переводит строку в байты
                                    .addLast(new CommandHandler());

                        }
                    });

            ChannelFuture future =  bootstrap.bind(SERVER_PORT).sync(); // фактический запуск свервера
            System.out.println("Сервер запущен");
            future.channel().closeFuture().sync(); // здесь ждем, пока свервер еще работает (чтобы не отваливаться)

        }catch (Exception e){
            System.out.println("Сервер упал");
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

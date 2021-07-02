package ru.gb.pugacheva.server.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import ru.gb.pugacheva.common.domain.PropertiesReciever;
import ru.gb.pugacheva.server.core.handler.CommandInboundHandler;
import ru.gb.pugacheva.server.factory.Factory;
import ru.gb.pugacheva.server.service.DatabaseConnectionService;
import sun.security.smartcardio.SunPCSC;

public class NettyServerService implements ServerService {

    //private static final int SERVER_PORT = 8189;

    private static final int SERVER_PORT = Integer.parseInt(PropertiesReciever.getProperties("port").trim());
    private static DatabaseConnectionService databaseConnectionService;

    public static DatabaseConnectionService getDatabaseConnectionService() {
        return databaseConnectionService;
    }

    @Override
    public void startServer() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel channel) {
                            channel.pipeline()
                                    .addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)))
                                    .addLast(new ObjectEncoder())
                                    .addLast(new CommandInboundHandler());

                        }
                    });

            ChannelFuture future = bootstrap.bind(SERVER_PORT).sync();
            System.out.println("Сервер запущен");
            databaseConnectionService= Factory.getDatabaseConnectionService(); // подключились к базе сразу
            future.channel().closeFuture().sync();


        } catch (Exception e) {
            System.out.println("Сервер упал");
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            databaseConnectionService.closeConnection(); //отклучились от базы в конце
        }
    }

}

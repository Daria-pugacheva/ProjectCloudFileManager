package ru.gb.pugacheva.client.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.gb.pugacheva.client.controller.Controller;
import ru.gb.pugacheva.client.factory.Factory;
import ru.gb.pugacheva.client.service.CommandDictionaryService;
import ru.gb.pugacheva.client.service.NetworkService;
import ru.gb.pugacheva.client.service.impl.ClientCommandDictionaryServiceImpl;
import ru.gb.pugacheva.client.service.impl.NettyNetworkService;
import ru.gb.pugacheva.common.domain.Command;

public class ClientCommandHandler extends SimpleChannelInboundHandler<Command> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command command) throws Exception {
        CommandDictionaryService commandDictionary = Factory.getCommandDictionary();
        commandDictionary.processCommand(command);

       System.out.println("нетти соединение сработало"); // для проверки
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        System.out.println(cause); // потом заменить на логер
    }
}

    //TODO: надо увязать этот хэндлер с тем, чтоб клиент в итоге получает или информацию о файлах на
    //сервере (заполняется правая таблица) или получает файл (и надо добавить файл к нам)



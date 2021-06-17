package ru.gb.pugacheva.client.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.gb.pugacheva.client.controller.Controller;
import ru.gb.pugacheva.client.service.NetworkService;
import ru.gb.pugacheva.client.service.impl.NettyNetworkService;

public class ClientHandler extends SimpleChannelInboundHandler<String> {

 // ТОЖЕ ПОКА КОД НЕ ПРИМЕНЯЕТСЯ . ИСПОЛЬЗУЮ ВАРИАНТ ПОДКЛЮЧЕНИЯ IO



    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String command) throws Exception {

       System.out.println("нетти соединение сработало");
    }

    //TODO: надо увязать этот хэндлер с тем, чтоб клиент в итоге получает или информацию о файлах на
    //сервере (заполняется правая таблица) или получает файл (и надо добавить файл к нам)

}

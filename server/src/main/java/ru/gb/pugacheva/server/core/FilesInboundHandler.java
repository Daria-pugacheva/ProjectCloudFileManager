package ru.gb.pugacheva.server.core;

import io.netty.channel.ChannelInboundHandlerAdapter;
import ru.gb.pugacheva.server.factory.Factory;
import ru.gb.pugacheva.server.service.CommandDictionaryService;

public class FilesInboundHandler extends ChannelInboundHandlerAdapter {

 private CommandDictionaryService dictionaryService;

    public FilesInboundHandler() {

        this.dictionaryService = Factory.getCommandDictionaryService();

    }

    // Входящий будет файл
}

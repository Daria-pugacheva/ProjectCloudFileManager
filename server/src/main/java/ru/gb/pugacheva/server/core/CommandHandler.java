package ru.gb.pugacheva.server.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.gb.pugacheva.server.factory.Factory;
import ru.gb.pugacheva.server.service.CommandDictionaryService;

public class CommandHandler extends SimpleChannelInboundHandler <String> {

    CommandDictionaryService dictionaryService;

    public CommandHandler() {
        this.dictionaryService = Factory.getCommandDictionaryService();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String command) throws Exception {
        String result = dictionaryService.processCommand(command);
        ctx.writeAndFlush(result);

    }
}

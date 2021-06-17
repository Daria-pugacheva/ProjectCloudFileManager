package ru.gb.pugacheva.client.factory;

import ru.gb.pugacheva.client.service.NetworkService;
import ru.gb.pugacheva.client.service.impl.IONetworkService;
import ru.gb.pugacheva.client.service.impl.NettyNetworkService;

public class Factory {

    public static NetworkService getNetworkService(){
        return IONetworkService.getInstance();
    }

//    public static NetworkService getNetworkService(){
//        return new NettyNetworkService();
//    }
}

package ru.gb.pugacheva.client.factory;

import ru.gb.pugacheva.client.service.NetworkService;
import ru.gb.pugacheva.client.service.impl.IONetworkService;

public class Factory {

    public static NetworkService getNetworkService(){
        return IONetworkService.getInstance();
    }
}

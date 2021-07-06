package ru.gb.pugacheva.server.service.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ServerPropertiesReciever {
    private static final String pathToProperties = "server/src/main/resources/server.properties";
    private static final Properties properties = new Properties();

    public static String getProperties (String propertyName){
        try (InputStream in = new FileInputStream(pathToProperties)){
            properties.load(in);
            return properties.getProperty(propertyName);
        }catch (IOException e){
            throw new IllegalArgumentException("Значение " + propertyName + " отсутствует");
        }
    }
}

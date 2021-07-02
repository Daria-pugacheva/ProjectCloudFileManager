package ru.gb.pugacheva.common.domain;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReciever {

    private static final String pathToProperties = "common/src/main/resources/config.properties";
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

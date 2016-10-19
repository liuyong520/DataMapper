package com.nnk.ecsys.database.mapper.generator.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by YHT on 2016/8/23.
 */
public class PropertiesUtils {
    public static Properties load(String configPath) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(configPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}

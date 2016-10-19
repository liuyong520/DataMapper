package com.nnk.ecsys.database.mapper.generator.main;

import com.nnk.ecsys.database.mapper.generator.common.PropertiesUtils;
import com.nnk.ecsys.database.mapper.generator.databases.DatabaseInit;
import com.nnk.ecsys.database.mapper.generator.entity.ConfigInfo;
import com.nnk.ecsys.database.mapper.generator.metaCommon.MetapomUpdater;
import com.nnk.ecsys.database.mapper.generator.mybatis.MybatisGenerator;
import org.apache.commons.lang3.ArrayUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by YHT on 2014/11/28.
 */
public class MapperMain {
    private static final String MAPPER_CONFIG_PATH = "config/mapperConfig.properties";

    public static void main(String[] argv) throws Exception {
        Properties properties = PropertiesUtils.load(MAPPER_CONFIG_PATH);
        if (ArrayUtils.isNotEmpty(argv) && argv.length >= 4) {
            properties.setProperty("mapper.moduleName", argv[0]);
            properties.setProperty("mapper.path.sql", argv[1]);
            properties.setProperty("mapper.path.modules", argv[2]);
            properties.setProperty("mapper.path.metaCommon", argv[3]);
        } else {
            System.out.println("请输入Mapper模块名称(SQL文件名，不带后缀)：");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            properties.setProperty("mapper.moduleName", bufferedReader.readLine());
        }
        ConfigInfo configInfo = new ConfigInfo(properties);
        //初始化数据库
        new DatabaseInit().initDatabase(configInfo);
        //动态生成mapper模块
        new MybatisGenerator().mybatisGeneratorCode(configInfo);
        //添加声明到metaCommon项目中
        try {
            new MetapomUpdater().updatePom(configInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.nnk.ecsys.database.mapper.generator.databases;

import com.nnk.ecsys.database.mapper.generator.common.DBUtils;
import com.nnk.ecsys.database.mapper.generator.entity.ConfigInfo;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * Created by YHT on 2016/8/3.
 */
public class DatabaseInit {

    public void initDatabase(ConfigInfo configInfo) throws IOException {
        DataSource dataSource = new DBUtils(configInfo.getMapperConfig()).getDataSource();
        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setEnabled(true);
        dataSourceInitializer.setDataSource(dataSource);
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
        resourceDatabasePopulator.setSqlScriptEncoding("UTF-8");
        resourceDatabasePopulator.addScript(new FileSystemResource(configInfo.getSqlFilePath()));
        dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);
        dataSourceInitializer.afterPropertiesSet();
    }
}

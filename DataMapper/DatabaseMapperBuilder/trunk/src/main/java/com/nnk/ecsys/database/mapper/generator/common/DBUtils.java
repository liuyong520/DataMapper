package com.nnk.ecsys.database.mapper.generator.common;

import com.alibaba.druid.pool.DruidDataSource;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by YHT on 2014/12/23.
 */
public class DBUtils {
    private DataSource dataSource;

    public DBUtils(Properties properties) {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(properties.getProperty("db.url"));
        druidDataSource.setUsername(properties.getProperty("db.username"));
        druidDataSource.setPassword(properties.getProperty("db.password"));
        druidDataSource.setInitialSize(1);
        druidDataSource.setMaxActive(10);
        try {
            druidDataSource.init();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dataSource = druidDataSource;

    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public Map<String, String> getColumnByTable(String tableName) {
        Map<String,String> resMap=new HashMap<String, String>();
        try {
            ResultSet resultSet = dataSource.getConnection().getMetaData().getColumns(null, "%", tableName, "%");
            while (resultSet.next()) {
                resMap.put(resultSet.getString("COLUMN_NAME"),resultSet.getString("TYPE_NAME"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resMap;
    }

}

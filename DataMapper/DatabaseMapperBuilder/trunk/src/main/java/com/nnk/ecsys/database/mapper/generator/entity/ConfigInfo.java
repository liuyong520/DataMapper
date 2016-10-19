package com.nnk.ecsys.database.mapper.generator.entity;

import java.util.Properties;

/**
 * Created by YHT on 2016/8/23.
 */
public class ConfigInfo {
    private Properties mapperConfig;
    private String moduleName;      //模块名称
    private String generatorPath; //mybatis-generator配置
    private String modulesPath;          //模块存放路径
    private String sqlFilePath;         //sql文件路径
    private String templatePath;        //模板路径
    private String metaCmmonPath;     //MetaCommon路径

    public ConfigInfo(Properties mapperConfig) {
        this.mapperConfig = mapperConfig;
        this.moduleName = mapperConfig.getProperty("mapper.moduleName");
        this.sqlFilePath = mapperConfig.getProperty("mapper.path.sql");
        this.modulesPath = mapperConfig.getProperty("mapper.path.modules");
        this.metaCmmonPath = mapperConfig.getProperty("mapper.path.metaCommon");
        this.generatorPath = mapperConfig.getProperty("mapper.path.generator");
        this.templatePath = mapperConfig.getProperty("mapper.path.template");
    }

    protected Properties formatProperties(Properties properties) {
        for (Object key : properties.keySet()) {
            properties.setProperty((String) key, properties.getProperty(key.toString()).trim());
        }
        return properties;
    }

    public Properties getMapperConfig() {
        return mapperConfig;
    }

    public void setMapperConfig(Properties mapperConfig) {
        this.mapperConfig = mapperConfig;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getGeneratorPath() {
        return generatorPath;
    }

    public void setGeneratorPath(String generatorPath) {
        this.generatorPath = generatorPath;
    }

    public String getModulesPath() {
        return modulesPath;
    }

    public void setModulesPath(String modulesPath) {
        this.modulesPath = modulesPath;
    }

    public String getSqlFilePath() {
        return String.format("%s/%s.sql", this.sqlFilePath, moduleName);
    }

    public void setSqlFilePath(String sqlFilePath) {
        this.sqlFilePath = sqlFilePath;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    public String getMetaCmmonPath() {
        return metaCmmonPath;
    }

    public void setMetaCmmonPath(String metaCmmonPath) {
        this.metaCmmonPath = metaCmmonPath;
    }
}

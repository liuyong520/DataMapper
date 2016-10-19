package com.nnk.ecsys.database.mapper.generator.entity;

import java.util.List;

/**
 * Created by YHT on 2014/11/28.
 */
public class ModuleInfo {
    private String moduleName;
    private List<String> tables;

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public List<String> getTables() {
        return tables;
    }

    public void setTables(List<String> tables) {
        this.tables = tables;
    }

    @Override
    public String toString() {
        return "ModuleInfo{" +
                "moduleName='" + moduleName + '\'' +
                ", tables=" + tables +
                '}';
    }
}

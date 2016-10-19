package com.nnk.ecsys.database.mapper.generator.mybatis;

import com.nnk.ecsys.database.mapper.generator.common.DBUtils;
import com.nnk.ecsys.database.mapper.generator.entity.ConfigInfo;
import com.nnk.ecsys.database.mapper.generator.entity.ModuleInfo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.mybatis.generator.api.ShellRunner;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by YHT on 2014/11/27.
 */
public class MybatisGenerator {
    private DBUtils dbUtils;

    /**
     * @param configInfo
     * @throws JDOMException
     * @throws IOException
     */
    public void mybatisGeneratorCode(ConfigInfo configInfo) throws JDOMException, IOException {
        List<ModuleInfo> moduleInfos = readModuleConfig(configInfo);
        dbUtils = new DBUtils(configInfo.getMapperConfig());
        for (ModuleInfo moduleInfo : moduleInfos) {
            System.out.println("modules:" + moduleInfo.getModuleName());
            //生成module
            createModule(configInfo, moduleInfo.getModuleName());
            //生成Mybatis.Generator配置
            File generatorConfig = createGeneratorConfig(configInfo, moduleInfo);
            ShellRunner.main(new String[]{"-configfile", generatorConfig.getAbsolutePath(), "-overwrite"});
        }
    }

    public File createGeneratorConfig(ConfigInfo configInfo, ModuleInfo moduleInfo) throws IOException, JDOMException {
        Properties properties = configInfo.getMapperConfig();
        List<String> fileLineList = FileUtils.readLines(new File(configInfo.getGeneratorPath()));
        //替换mybatis-generator变量
        Pattern pattern = Pattern.compile("\\$\\{([^$()]+)\\}");
        for (int i = 0; i < fileLineList.size(); i++) {
            String lineStr = fileLineList.get(i);
            Matcher matcher = pattern.matcher(lineStr);
            StringBuffer stringBuffer = new StringBuffer();
            int index = 0;
            while (matcher.find()) {
                String valName = matcher.group(1);
                if (properties.containsKey(valName)) {
                    stringBuffer.append(lineStr.substring(index, matcher.start()));
                    stringBuffer.append(properties.get(valName));
                    index = matcher.end();
                }
            }
            stringBuffer.append(lineStr.substring(index, lineStr.length()));
            fileLineList.set(i, stringBuffer.toString());
        }
        File newGeneratorFile = File.createTempFile(moduleInfo.getModuleName(), ".xml");
        FileUtils.writeLines(newGeneratorFile, fileLineList);
        System.out.println(newGeneratorFile.getAbsolutePath());
        SAXBuilder builder = new SAXBuilder();
        builder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        Document document = builder.build(newGeneratorFile);//获得文档对象
        addTalbe(document, moduleInfo.getTables());
        new XMLOutputter().output(document, new FileOutputStream(newGeneratorFile));
        return newGeneratorFile;
    }

    public void addTalbe(Document document, List<String> tables) {
        Element root = document.getRootElement();//获得根节点
        Element context = root.getChild("context");
        Element table = context.getChild("table").clone();
        context.removeChildren("table");
        for (String tableName : tables) {
            Element tmpTable = table.clone();
            tmpTable.setAttribute("tableName", tableName);
            tmpTable.setAttribute("domainObjectName", getClassNameByTableName(tableName));
            //检查字段类型
            for (Element tableChild : this.filterTableColumn(tableName)) {
                tmpTable.addContent(tableChild);
            }
            context.addContent(tmpTable);
        }
    }

    public List<Element> filterTableColumn(String tableName) {
        List<Element> resList = new ArrayList<Element>();
        for (Map.Entry<String, String> entry : dbUtils.getColumnByTable(tableName).entrySet()) {
            if ("TEXT".equalsIgnoreCase(entry.getValue())) {
                Element element = new Element("columnOverride");
                element.setAttribute("column", entry.getKey());
                element.setAttribute("jdbcType", "VARCHAR");
                resList.add(element);
            }
        }
        return resList;
    }

    public String getClassNameByTableName(String tableName) {
        if (StringUtils.isEmpty(tableName)) {
            return tableName;
        }
        StringBuilder resStr = new StringBuilder();
        String[] argv = tableName.split("_");
        if (argv.length > 2) {
            argv = ArrayUtils.subarray(argv, 1, argv.length);
        }
        for (String str : argv) {
            resStr.append(str.substring(0, 1).toUpperCase()).append(str.substring(1, str.length()));
        }
        return resStr.toString();
    }

    public void createModule(final ConfigInfo configInfo, final String moduleName) throws IOException, JDOMException {
        //检查模块是否存在，如不存在则创建
        File moduleDir = new File(configInfo.getModulesPath(), moduleName);
        if (moduleDir.isDirectory() && moduleDir.exists()) {
            FileUtils.deleteDirectory(moduleDir);
        }
        moduleDir.mkdirs();
        FileUtils.copyDirectory(new File(configInfo.getTemplatePath()), moduleDir);
        moduleDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if ("moduleTemplate.iml".equals(pathname.getName())) {
                    pathname.renameTo(new File(pathname.getParentFile(), String.format("%s.iml", moduleName)));
                } else if ("pom.xml".equals(pathname.getName())) {
                    SAXBuilder builder = new SAXBuilder();
                    try {
                        Document document = builder.build(pathname);//获得文档对象
                        Element root = document.getRootElement();//获得根节点
                        Namespace namespace = Namespace.getNamespace("http://maven.apache.org/POM/4.0.0");
                        Element artifactId = root.getChild("artifactId", namespace);
                        artifactId.setText(moduleName);
                        new XMLOutputter().output(document, new FileOutputStream(pathname));
                    } catch (JDOMException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });
    }

    public List<ModuleInfo> readModuleConfig(ConfigInfo configInfo) throws JDOMException, IOException {
        File file = new File(configInfo.getSqlFilePath());
        if (!file.exists() || !file.isFile()) {
            throw new RuntimeException(file.getAbsolutePath() + "文件不存在");
        }
        List<String> fileLineList = FileUtils.readLines(file);
        Pattern pattern = Pattern.compile("^CREATE\\s+TABLE\\s+`?(\\w+)`?.*$");
        List<String> tableList = new ArrayList<String>();
        for (String lineStr : fileLineList) {
            Matcher matcher = pattern.matcher(lineStr);
            if (matcher.find()) {
                tableList.add(matcher.group(1));
            }
        }
        if (tableList.isEmpty()) {
            return new ArrayList<>();
        } else {
            final ModuleInfo moduleInfo = new ModuleInfo();
            moduleInfo.setTables(tableList);
            moduleInfo.setModuleName(configInfo.getModuleName());
            return new ArrayList<ModuleInfo>() {{
                add(moduleInfo);
            }};
        }
    }

    public static void main(String[] args) {
        Map<String, String> properties = new HashMap<>();
        properties.put("module1.info", "1");
        properties.put("module2", "2");
        properties.put("module3", "3");
        String lineStr = "EEE${module1.info}AAA${module2}CCC${module3}";
        Pattern pattern = Pattern.compile("\\$\\{([^$()]+)\\}");
        Matcher matcher = pattern.matcher(lineStr);
        StringBuffer stringBuffer = new StringBuffer();
        int index = 0;
        while (matcher.find()) {
            String valName = matcher.group(1);
            if (properties.containsKey(valName)) {
                stringBuffer.append(lineStr.substring(index, matcher.start()));
                stringBuffer.append(properties.get(valName));
                index = matcher.end();
            }
        }
        stringBuffer.append(lineStr.substring(index, lineStr.length()));
        System.out.println(stringBuffer.toString());
    }
}

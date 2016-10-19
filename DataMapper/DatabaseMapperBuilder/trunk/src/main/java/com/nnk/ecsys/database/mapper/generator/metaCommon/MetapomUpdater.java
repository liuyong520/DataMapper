package com.nnk.ecsys.database.mapper.generator.metaCommon;

import com.nnk.ecsys.database.mapper.generator.entity.ConfigInfo;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Created by YHT on 2016/8/24.
 */
public class MetapomUpdater {
    /**
     * 更新pom.xml文件
     *
     * @param configInfo
     */
    public void updatePom(ConfigInfo configInfo) throws Exception {
        File metapomFile = new File(configInfo.getMetaCmmonPath(), "pom.xml");
        if (!metapomFile.exists() || !metapomFile.isFile()) {
            throw new Exception("metaCommon-pom.xml文件不存在");
        }


        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(metapomFile);//获得文档对象
        Namespace mavenNamespace = Namespace.getNamespace("", "http://maven.apache.org/POM/4.0.0");
        Element root = document.getRootElement();//获得根节点
        root.removeNamespaceDeclaration(mavenNamespace);
        Element dependenciesElement = root.getChild("dependencyManagement", mavenNamespace).getChild("dependencies", mavenNamespace);
        List<Element> dependencieElementList = dependenciesElement.getChildren("dependency", mavenNamespace);
        boolean moduleAbsent = false;
        for (Element dependencie : dependencieElementList) {
            if ("com.nnk.ecsys.database.mapper".equals(dependencie.getChildText("groupId", mavenNamespace))) {
                if (configInfo.getModuleName().equals(dependencie.getChildText("artifactId", mavenNamespace))) {
                    moduleAbsent = true;
                    break;
                }
            }
        }
        if (!moduleAbsent) {
            Element moduleElement = new Element("dependency", mavenNamespace);
            moduleElement.addContent(new Element("groupId", mavenNamespace).setText("com.nnk.ecsys.database.mapper"));
            moduleElement.addContent(new Element("artifactId", mavenNamespace).setText(configInfo.getModuleName()));
            moduleElement.addContent(new Element("version", mavenNamespace).setText("${databaseMapper.version}"));
            dependenciesElement.addContent(moduleElement);

            XMLOutputter xmlOutputter = new XMLOutputter();
            xmlOutputter.setFormat(Format.getPrettyFormat());
            xmlOutputter.output(document, new FileOutputStream(metapomFile));
        }
    }
}

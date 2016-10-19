package com.nnk.ecsys.database.mapper.generator;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by YHT on 2014/12/2.
 */
public class FileUtilsTest {

    @Test
    public void deleteDirectory() throws IOException {
        File file=new File("D:\\test");
        FileUtils.deleteDirectory(file);
    }

}

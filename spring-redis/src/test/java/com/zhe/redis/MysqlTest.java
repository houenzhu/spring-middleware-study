package com.zhe.redis;

import com.zhe.redis.service.TestTableService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SpringBootTest
public class MysqlTest {

    @Autowired
    private TestTableService testTableService;


    @Test
    public void readSql() {
        testTableService.test();
    }

    @Test
    public void getFiles() {
        try {
            File sqlFile = new ClassPathResource("sql").getFile();
            List<File> files = Arrays.stream(Objects.requireNonNull(sqlFile.listFiles())).toList();
            List<String> fileList = files.stream()
                    .map(File::getName)
                    .filter(name -> StringUtils.endsWithIgnoreCase(name, ".sql")).toList();
            System.out.println(fileList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

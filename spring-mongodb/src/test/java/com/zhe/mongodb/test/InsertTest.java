package com.zhe.mongodb.test;

import com.zhe.mongodb.entity.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@SpringBootTest
public class InsertTest {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void m1() {
        Person person = Person.builder().name("张三").age(18).id(1L).build();
        mongoTemplate.insert(person);
    }

    /**
     * 有则更新，无则新增
     */
    @Test
    void m2() {
        Person person = Person.builder().name("张三1").age(18).id(1L).build();
        mongoTemplate.save(person);
    }

    /**
     * 自定义集合
     * 插入文档
     * 相当于将person 这个集合复制到person111里
     */
    @Test
    void m3() {
        Person person = Person.builder().name("张三").age(18).id(1L).build();
        mongoTemplate.insert(person, "person111");
    }

    @Test
    void m4() {
        List<Person> persons = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Person person = Person.builder().name("张三" + i).age(i).id((long) i).build();
            if (i == 2) {
                person.setAddress("上海");
            }
            persons.add(person);
        }
        Collection<Person> people = mongoTemplate.insertAll(persons);
        people.stream().sorted(Comparator.comparing(Person::getId)).forEach(System.out::println);
    }
}

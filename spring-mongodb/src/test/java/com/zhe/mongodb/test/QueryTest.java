package com.zhe.mongodb.test;

import com.zhe.mongodb.entity.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@SpringBootTest
public class QueryTest {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void m1() {
        Criteria name = Criteria.where("name").regex("^张三");
        Criteria age = Criteria.where("age").gte(18).lte(19);
        Criteria criteria = new Criteria().andOperator(name, age);
        Query query = new Query(criteria).with(Sort.by("id"));
        List<Person> people = mongoTemplate.find(query, Person.class);
        people.forEach(System.out::println);
    }
}

package com.zhe.mongodb.test;

import com.mongodb.client.result.DeleteResult;
import com.zhe.mongodb.entity.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@SpringBootTest
public class DeleteTests {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void m1() {
        Query query = new Query(Criteria.where("id").is(1L));
        DeleteResult remove = mongoTemplate.remove(query, Person.class);
        System.out.println(remove.getDeletedCount());
    }

    @Test
    void m2() {
        Query query = new Query(Criteria.where("age").lt(18));
        DeleteResult remove = mongoTemplate.remove(query, Person.class);
        System.out.println(remove.getDeletedCount());
    }

    @Test
    void m3() {
        Query query = new Query(Criteria.where("id").is(2L));
        Person person = mongoTemplate.findAndRemove(query, Person.class);
        System.out.println(person);
    }

    @Test
    void m4() {
        Query query = new Query(Criteria.where("id").lte(4L));
        List<Person> personList = mongoTemplate.findAllAndRemove(query, Person.class);
        personList.forEach(System.out::println);
    }
}

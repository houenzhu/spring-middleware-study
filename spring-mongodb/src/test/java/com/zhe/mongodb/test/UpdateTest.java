package com.zhe.mongodb.test;

import com.zhe.mongodb.entity.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@SpringBootTest
public class UpdateTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void m1() {
        Person person = mongoTemplate.findById(1L, Person.class);
        person.setName("武松");
        mongoTemplate.save(person);
        Person person1 = mongoTemplate.findById(1L, Person.class);
        System.out.println("person1 = " + person1);
    }

    @Test
    void m2() {
        Query query = new Query(Criteria.where("id").is(1L));
        Update update = new Update().set("name", "松江");
        mongoTemplate.updateFirst(query, update, Person.class);
        Person person = mongoTemplate.findById(1L, Person.class);
    }

    @Test
    void m3() {
        // >=
        Query query = new Query(Criteria.where("id").gte(1L));
        Update update = new Update().set("name", "松江");
        // 批量更新
        mongoTemplate.updateMulti(query, update, Person.class);
    }

    @Test
    void m4() {
        Query query = new Query(Criteria.where("id").gt(1L));
        Update update = new Update().set("_class", "com.zhe.mongodb.entity.Person");
        mongoTemplate.updateMulti(query, update, Person.class);
    }
}

package com.zhe.mongodb.controller;

import com.zhe.mongodb.entity.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mongo")
public class MongoController {
    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("/{name}")
    public List<Person> getPersonById(@PathVariable(value = "name") String name) {
        Query query = new Query(Criteria.where("name").is(name));
        return mongoTemplate.find(query, Person.class);
    }
}

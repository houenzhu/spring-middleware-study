package com.zhe.mongodb.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.annotation.Collation;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Collation("person")
public class Person {
    @Id
    private Long id;
    private String name;
    private Integer age;
    private String address;
}

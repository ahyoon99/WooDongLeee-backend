package com.woodongleee.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateUserRes {
    private String name;
    private int age;
    private String gender;
    private String email;
    private String id;
    private String password;
    private String town;
}

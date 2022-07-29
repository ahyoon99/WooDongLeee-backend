package com.woodongleee.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetUserByJwtRes {
    private String name;
    private int age;
    private String gender;
    private String email;
    private String id;
    private String town;
    private String introduce;
    private String teamName;
    private String teamProfileImgUrl;
    private String status;
}

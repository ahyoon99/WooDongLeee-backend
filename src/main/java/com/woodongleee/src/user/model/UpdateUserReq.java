package com.woodongleee.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpdateUserReq {
    private String name;
    private int age;
    private String gender;
    private String town;
    private String introduce;
    private String profileImgUrl;
}

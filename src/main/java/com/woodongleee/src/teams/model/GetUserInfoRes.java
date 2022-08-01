package com.woodongleee.src.teams.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetUserInfoRes {
    private int userIdx; //유저 식별자
    private String name; //이름
    private String email; //이메일
    private String id; //아이디
    private String profileImgUrl; //프로필 이미지
    private String town; //동네
    private String introduce; //소개글
    private String gender; //성별
    private int age; // 나이
}

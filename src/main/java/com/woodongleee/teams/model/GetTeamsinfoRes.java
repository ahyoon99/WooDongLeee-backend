package com.woodongleee.teams.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetTeamsinfoRes {
    private int teamIdx; //팀 식별자
    private String name; //팀 이름
    private String town; //동네
    private int teamScore; // 팀 점수
    private String teamProfileImgUrl;
    private String introduce;// 팀 소개
    private boolean isRecruiting; // 팀원 모집여부
    private String status; //팀 해체: INACTIVE
}

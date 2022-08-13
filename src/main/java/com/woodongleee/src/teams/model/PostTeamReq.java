package com.woodongleee.src.teams.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostTeamReq {
    private String name;
    private String town;
    private String teamProfileImgUrl;
    private String introduce;
}

package com.woodongleee.src.teams_2.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class TeamApplyListRes {
    private int teamApplyIdx;
    private int userIdx;
    private String status;
}

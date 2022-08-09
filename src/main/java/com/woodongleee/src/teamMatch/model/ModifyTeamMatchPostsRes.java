package com.woodongleee.src.teamMatch.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ModifyTeamMatchPostsRes {
    private int matchPostIdx;   // 매칭글 식별자
    private String type;        // 매칭글 종류 식별자(TEAM)

    @Override
    public String toString() {
        return "ModifyTeamMatchPostsRes{" +
                "matchPostIdx=" + matchPostIdx +
                ", type='" + type + '\'' +
                '}';
    }
}
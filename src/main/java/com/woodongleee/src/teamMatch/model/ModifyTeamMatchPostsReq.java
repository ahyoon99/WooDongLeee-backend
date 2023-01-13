package com.woodongleee.src.teamMatch.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ModifyTeamMatchPostsReq {
    private int userIdx;    // 작성자 식별자
    private int teamScheduleIdx;    // 팀 일정(경기) 식별자
    private String contents;    // 설명글

    @Override
    public String toString() {
        return "ModifyTeamMatchPostsReq{" +
                "userIdx=" + userIdx +
                ", teamScheduleIdx=" + teamScheduleIdx +
                ", contents='" + contents + '\'' +
                '}';
    }

    public boolean checkUserIdxAndUserIdxByJWT(int userIdxByJwt){
        return this.userIdx == userIdxByJwt;
    }

    public boolean checkContentsMaxLength(int POST_LENGTH_MAX){
        return this.contents.length() > POST_LENGTH_MAX;
    }
}

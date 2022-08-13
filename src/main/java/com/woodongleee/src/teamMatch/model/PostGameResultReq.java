package com.woodongleee.src.teamMatch.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostGameResultReq {
    private int teamScheduleIdx;    // 팀 일정(경기) 식별자
    private int homeScore;          // 홈팀 점수
    private int awayScore;          // 상대팀 점수

    @Override
    public String toString() {
        return "PostGameResultReq{" +
                "teamScheduleIdx=" + teamScheduleIdx +
                ", homeScore=" + homeScore +
                ", awayScore=" + awayScore +
                '}';
    }
}

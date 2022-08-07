package com.woodongleee.src.userMatch.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class GetUserMatchPostInfoRes {
    private String status; // 내가 신청 했는지
    private int teamScheduleIdx;
    private int matchPostIdx;
    private String teamName;
    private String address;
    private String opponentTeamName;
    private int recruitCnt; // 현재 추가로 참여 가능한 인원
    private String contents;
    private String startTime;
    private String endTime;
    private String profileImgUrl;
}

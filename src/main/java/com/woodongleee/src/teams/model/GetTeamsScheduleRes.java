package com.woodongleee.src.teams.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetTeamsScheduleRes {
    private int teamIdx; //팀 식별자
    private String awayName; //상대팀 이름
    private String address; // 경기장 주소
    private String startTime; // 경기 시작 시간
    private String endTime; // 경기 종료 시간
    private String date; // 경기 날짜
    private int headCnt; // 인원 수
    private int joinCnt; // 참여 인원
    private int userMatchCnt; //용병 인원

}

package com.woodongleee.src.teams.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetTeamScheduleInfoRes {
    private int teamScheduleIdx; // 팀 일정
    private int homeIdx; //팀 식별자
    private String address; // 경기장 주소
    private int awayIdx; //상대 팀 식별자
    private String startTime; //경기 시작 시간
    private String endTIme; //경기 종료 시간
    private String date; //경기 날짜
    private int headCnt; //인원수
    private int joinCnt; //참여인원
    private int userMatchCnt; // 용병인원

}

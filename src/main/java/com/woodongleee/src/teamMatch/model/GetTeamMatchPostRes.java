package com.woodongleee.src.teamMatch.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetTeamMatchPostRes {
    private int teamScheduleIdx;
    private Boolean isMyPost;
    private String homeTeamName;
    private String address;
    private String startTime;
    private String endTime;
    private int headCnt;
    private String contents;
    private String status;

    @Override
    public String toString() {
        return "GetTeamMatchPostRes{" +
                "teamScheduleIdx=" + teamScheduleIdx +
                ", isMyPost=" + isMyPost +
                ", homeTeamName='" + homeTeamName + '\'' +
                ", address='" + address + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", headCnt=" + headCnt +
                ", contents='" + contents + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}

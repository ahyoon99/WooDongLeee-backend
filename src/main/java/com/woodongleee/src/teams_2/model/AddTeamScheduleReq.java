package com.woodongleee.src.teams_2.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

@Setter
@Getter
@AllArgsConstructor
public class AddTeamScheduleReq {
    private String address;
    private String startTime;
    private String endTime;
    private String date;
    private int headCnt;
}

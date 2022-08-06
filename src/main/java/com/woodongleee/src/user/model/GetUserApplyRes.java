package com.woodongleee.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetUserApplyRes {
    private String homeName;
    private String awayName;
    private String address;
    private String startTime;
    private String endTime;
    private String date;
    private String type;
    private String status;
}


package com.woodongleee.src.userMatch.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserMatchApplyInfo {
    private int matchApplyIdx;
    private int matchPostIdx;
    private int userIdx;
    private String name;
    private String status;
}

package com.woodongleee.src.userMatch.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CheckUserMatchApplyPossibilityRes {
    private String applyStatus;
    private int count;
    private String startTime;
    private int status;
}

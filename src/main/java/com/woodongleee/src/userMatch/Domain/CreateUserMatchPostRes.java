package com.woodongleee.src.userMatch.Domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateUserMatchPostRes {
    private int matchPostIdx;
    private String type;
}

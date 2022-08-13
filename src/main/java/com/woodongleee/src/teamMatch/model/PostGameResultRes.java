package com.woodongleee.src.teamMatch.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostGameResultRes {
    private int gameResultIdx;  // 추가된 경기 결과 Idx

    @Override
    public String toString() {
        return "PostGameResultRes{" +
                "gameResultIdx=" + gameResultIdx +
                '}';
    }
}
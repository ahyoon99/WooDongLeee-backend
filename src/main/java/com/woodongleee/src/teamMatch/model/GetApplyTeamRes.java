package com.woodongleee.src.teamMatch.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetApplyTeamRes {
    private int teamIdx;
    private String name;
    private String town;
    private int teamScore;
    private String teamProfileImgUrl;
    private String introduce;
    private String status;

    @Override
    public String toString() {
        return "GetApplyTeamRes{" +
                "teamIdx=" + teamIdx +
                ", name='" + name + '\'' +
                ", town='" + town + '\'' +
                ", teamScore=" + teamScore +
                ", teamProfileImgUrl='" + teamProfileImgUrl + '\'' +
                ", introduce='" + introduce + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}

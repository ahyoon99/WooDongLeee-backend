package com.woodongleee.config;

import lombok.Getter;

@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),

    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2003,"권한이 없는 유저의 접근입니다."),


    POSTS_EMPTY_POST_ID(false, 2020, "게시물 아이디의 값을 확인해주세요"),
    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),

    /**
     * 5000 : team-match
     */
    POST_POSTS_INVALID_CONTENTS(false, 5001, "내용의 글자수를 확인해주세요."),
    ALREADY_EXIST_TEAM_MATCH_POST(false, 5002, "이미 팀 매칭글이 생성된 경기입니다."),
    USER_NOT_LEADER(false, 5003, "리더만 가능한 기능입니다."),
    NO_EXIST_TEAM_MATCH(false, 5004, "존재하지 않는 팀 일정(경기)입니다."),
    FINISH_POST_PERIOD(false, 5005, "글 작성 기한이 지났습니다.");


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}

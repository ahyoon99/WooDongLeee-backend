package com.woodongleee.config;

import lombok.Getter;

@Getter
public enum BaseResponseStatus {
    /**
     * 2000 : 요청 성공
     */
    SUCCESS(true, 2000, "요청에 성공하였습니다."),

    /**
     * 3000 : 클라이언트 에러
     */
    EMPTY_PARAMETER(false, 3000, "빈칸을 채워주세요."),
    EMPTY_JWT(false, 3001, "JWT가 없습니다."),
    INVALID_JWT(false, 3002, "유효하지 않은 JWT입니다."),
    UNAUTHORIZED_ACCESS(false, 3003, "권한이 없는 유저의 접근입니다."),
    WRONG_PASSWORD(false, 3004, "비밀번호가 틀립니다"),
    INVALID_EMAIL_VERIFY_CODE(false, 3005, "인증 코드가 틀립니다."),
    DUPLICATED_EMAIL(false, 3010, "중복된 이메일 입니다."),
    DUPLICATED_ID(false, 3011, "중복된 아이디 입니다."),
    DUPLICATED_TEAM_NAME(false, 3012, "중복된 팀 이름 입니다."),
    INVALID_EMAIL_PATTERN(false, 3020, "이메일 형식을 확인해주세요."),
    INVALID_ID_PATTERN(false, 3021, "아이디 형식을 확인해주세요."),
    INVALID_PASSWORD_PATTERN(false, 3022, "비밀번호 길이를 확인해주세요."),
    INVALID_TEAM_NAME_PATTERN(false, 3023, "팀 이름 형식을 확인해주세요."),
    INVALID_SCORE_SCOPE(false, 3024, "입력한 점수의 범위가 잘못되었습니다."),
    ID_DOES_NOT_EXIST(false, 3030, "존재하지 않는 아이디입니다."),
    USER_DOES_NOT_EXIST(false, 3031, "존재하지 않는 유저입니다."),
    LEAVED_USER(false, 3032, "탈퇴한 유저입니다."),
    TEAM_DOES_NOT_EXIST(false, 3033, "존재하지 않는 팀입니다."),
    SCHEDULE_DOES_NOT_EXIST(false, 3034, "존재하지 않는 일정입니다."),
    MATCHING_DOES_NOT_EXIST(false, 3035, "존재하지 않는 경기매칭입니다."),
    MATCH_APPLY_DOES_NOT_EXIST(false, 3036, "존재하지 않는 매칭신청입니다."),
    TEAM_APPLY_DOES_NOT_EXIST(false, 3037, "존재하지 않는 팀 참가신청입니다."),
    EMAIL_VERIFY_REQUEST_DOES_NOT_EXIST(false, 3038, "존재하지 않는 이메일 인증 신청입니다."),
    MATCH_APPLY_PERIOD_ERROR(false, 3040, "용병/팀 모집 기간이 아닙니다."),
    MATCH_CREATE_PERIOD_ERROR(false, 3041, "용병/팀 모집글/매칭글 작성 기간이 아닙니다."),
    MATCH_APPLY_VOTE_PERIOD_ERROR(false, 3042, "참가 신청 투표 기한이 지났습니다."),
    ACCEPT_NOT_AVAILABLE(false, 3043, "해당 요청을 승인 할 수 없습니다."),
    DECLINE_NOT_AVAILABLE(false, 3044, "해당 요청을 거절 할 수 없습니다."),
    CANCEL_NOT_AVAILABLE(false, 3045, "해당 요청을 취소 할 수 없습니다."),
    EMAIL_VERIFY_REQUEST_EXPIRED(false, 3046, "인증기간이 만료되었습니다."),
    MATCH_ALREADY_EXIST(false, 3050, "이미 용병/팀 매칭중인 경기입니다."),
    SCHEDULE_ALREADY_EXIST(false, 3051, "해당 시간에 이미 일정이 있습니다."),
    TEAM_ALREADY_EXIST(false, 3052, "이미 팀에 가입되어 있습니다"),
    TEAM_NOT_RECRUITING(false, 3060, "팀원 모집중이 아닌 팀입니다."),
    NOT_TEAMMATE(false, 3061, "팀원이 아닙니다."),
    TEAM_DISMISSED(false, 3062, "이미 해체된 팀입니다."),
    MATCH_NOT_FINISHED(false, 3063, "아직 종료되지 않은 경기입니다."),
    POST_POSTS_INVALID_CONTENTS(false, 6034, "내용의 글자수를 확인해주세요."),

    /**
     * 4000 : 서버 에러
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    PASSWORD_ENCRYPTION_ERROR(false, 4001, "비밀번호 암호화에 실패하였습니다."),
    EMAIL_SERVER_ERROR(false, 4002, "이메일 전송에 실패했습니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
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
    INVALID_EMAIL_VERIFY_CODE(false, 3005, "인증 코드가 틀립니다."),
    DUPLICATED_EMAIL(false, 3010, "중복된 이메일 입니다."),
    DUPLICATED_ID(false, 3011, "중복된 아이디 입니다."),
    INVALID_EMAIL_PATTERN(false, 3020, "이메일 형식을 확인해주세요."),
    INVALID_ID_PATTERN(false, 3021, "아이디 형식을 확인해주세요."),
    INVALID_PASSWORD_PATTERN(false, 3022, "비밀번호 길이를 확인해주세요."),
    EMAIL_VERIFY_REQUEST_NOT_EXIST(false, 3038, "존재하지 않는 이메일 인증 신청입니다."),
    EMAIL_VERIFY_REQUEST_EXPIRED(false, 3046, "인증기간이 만료되었습니다."),
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

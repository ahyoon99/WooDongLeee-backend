package com.woodongleee.src.user;

import com.woodongleee.config.BaseException;
import com.woodongleee.config.BaseResponse;
import com.woodongleee.config.BaseResponseStatus;
import com.woodongleee.src.user.model.VerifyDomain;
import com.woodongleee.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

import static com.woodongleee.config.BaseResponseStatus.EMAIL_VERIFY_REQUEST_EXPIRED;


@Service
public class UserProvider {

    private final UserDao userDao;
    private final JwtService jwtService;

    @Autowired
    public UserProvider(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService;
    }
    public boolean isIdDuplicated(String id) {
        return userDao.isIdDuplicated(id) == 1;
    }

    public boolean isEmailDuplicated(String email) {
        return userDao.isEmailDuplicated(email) == 1;
    }

    //이메일 인증 코드 중복 검사 -> 중복시 이전 인증 코드 삭제
    public boolean isEmailVerifyCodeRequestDuplicated(String email){
        return userDao.isEmailVerifyCodeRequestDuplicated(email) == 1;
    }

    //이메일 인증 코드 인증
    public boolean verify(String email, String code) throws BaseException {
        VerifyDomain verifyDomain = userDao.verify(email);
        if(verifyDomain.getExpirationTime().after(new Timestamp(System.currentTimeMillis()))){
            return verifyDomain.getCode().equals(code);
        }
        else{
            throw new BaseException(EMAIL_VERIFY_REQUEST_EXPIRED);
        }
    }
}

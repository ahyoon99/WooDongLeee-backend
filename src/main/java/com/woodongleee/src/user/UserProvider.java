package com.woodongleee.src.user;

import com.woodongleee.config.BaseException;
import com.woodongleee.src.user.model.*;
import com.woodongleee.utils.JwtService;
import com.woodongleee.utils.SHA256;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.woodongleee.config.BaseResponseStatus.*;


@Service
public class UserProvider {

    private final UserDao userDao;
    private final JwtService jwtService;

    @Autowired
    public UserProvider(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService;
    }
    public boolean isIdDuplicated(String id) throws BaseException {
        try{
            return userDao.isIdDuplicated(id) == 1;
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public boolean isEmailDuplicated(String email) throws BaseException {
        try{
            return userDao.isEmailDuplicated(email) == 1;
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //이메일 인증 코드 중복 검사 -> 중복시 이전 인증 코드 삭제
    public boolean isEmailVerifyCodeRequestDuplicated(String email) throws BaseException {
        try {
            return userDao.isEmailVerifyCodeRequestDuplicated(email) == 1;
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
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

    public UserLoginRes login(UserLoginReq userLoginReq) throws BaseException {
        String password;
        try{
            //암호화
            password = SHA256.encrypt(userLoginReq.getPassword());
            userLoginReq.setPassword(password);
        } catch (Exception exception) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }

        try{
            UserLoginUserIdxAndPassword userInfo = userDao.login(userLoginReq.getId());
            if(password.equals(userInfo.getPassword())){
                String jwt = jwtService.createJwt(userInfo.getUserIdx());
                return new UserLoginRes(userInfo.getUserIdx(), jwt);
            }
            else{
                throw new BaseException(WRONG_PASSWORD);
            }
        } catch (EmptyResultDataAccessException e){
            throw new BaseException(ID_DOES_NOT_EXIST);
        }

    }

    public GetUserByJwtRes getUserByJwt(int userIdx) throws BaseException {
        if(checkUserExist(userIdx) == 0){
            throw new BaseException(USER_DOES_NOT_EXIST);
        }
        if(checkUserStatus(userIdx).equals("INACTIVE")){
            throw new BaseException(LEAVED_USER);
        }

        try{
            return userDao.getUserByJwt(userIdx);
        } catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public String checkUserStatus(int userIdx) throws BaseException {
        try{
            return userDao.checkUserStatus(userIdx);
        } catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public int checkUserExist(int userIdx) throws BaseException {
        try{
            return userDao.checkUserExist(userIdx);
        } catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public boolean checkPassword(int userIdx, String currentPassword) throws BaseException {
        try{
            //암호화
            currentPassword = SHA256.encrypt(currentPassword);
        } catch (Exception exception) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }

        try{
            return userDao.checkPassword(userIdx).equals(currentPassword);
        } catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }


    public List<GetUserScheduleRes> getUserSchedule(int userIdx) throws BaseException {
        if (checkUserExist(userIdx) == 0) {
            throw new BaseException(USER_DOES_NOT_EXIST);
        }
        if (checkUserStatus(userIdx).equals("INACTIVE")) {
            throw new BaseException(LEAVED_USER);
        }
        try {
            return userDao.getUserSchedule(userIdx);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetIdByEmailRes getIdByEmail(String email) throws BaseException {
        try{
            return userDao.getIdByEmail(email);
        } catch (EmptyResultDataAccessException e){
            throw new BaseException(EMAIL_DOES_NOT_EXIST);
        } catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}

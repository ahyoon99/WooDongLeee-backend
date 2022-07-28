package com.woodongleee.src.user;

import com.woodongleee.config.BaseException;
import com.woodongleee.src.user.model.CreateUserReq;
import com.woodongleee.src.user.model.UserLoginReq;
import com.woodongleee.utils.JwtService;
import com.woodongleee.utils.SHA256;
import org.springframework.stereotype.Service;

import static com.woodongleee.config.BaseResponseStatus.*;

@Service
public class UserService {

    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;

    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;

    }
    public void createUser(CreateUserReq newUser) throws BaseException {

        if(userProvider.isIdDuplicated(newUser.getId())){
            throw new BaseException(DUPLICATED_ID);
        }
        if(userProvider.isEmailDuplicated(newUser.getEmail())){
            throw new BaseException(DUPLICATED_EMAIL);
        }
        if(!userProvider.verify(newUser.getEmail(), newUser.getCode())){
            throw new BaseException(INVALID_EMAIL_VERIFY_CODE);
        }

        else{
            String password;
            try{
                //암호화
                password = SHA256.encrypt(newUser.getPassword());
                newUser.setPassword(password);
            } catch (Exception exception) {
                throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
            }
            try{
                userDao.createUser(newUser);
            } catch (Exception exception){
                throw new BaseException(DATABASE_ERROR);
            }
        }
    }

    //인증 코드 생성
    public void createCode(String email, String code) throws BaseException {
        if(userProvider.isEmailVerifyCodeRequestDuplicated(email)){
            int result = userDao.deleteDuplicatedEmail(email);
            if(result != 1){
                throw new BaseException(DATABASE_ERROR);
            }
        }
        try{
            userDao.createCode(email, code);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

}

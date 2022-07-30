package com.woodongleee.src.userMatch;

import com.woodongleee.config.BaseException;
import com.woodongleee.config.BaseResponseStatus;
import com.woodongleee.src.userMatch.Domain.GetUserMatchPostInfoRes;
import com.woodongleee.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserMatchProvider {

    private final UserMatchDao userMatchDao;
    private final JwtService jwtService;

    @Autowired
    public UserMatchProvider(UserMatchDao userMatchDao, JwtService jwtService){
        this.userMatchDao = userMatchDao;
        this.jwtService = jwtService;
    }
    public List<GetUserMatchPostInfoRes> getUserMatchPosts(int userIdx, String town, String startTime, String endTime) throws BaseException {
        try{
            return userMatchDao.getUserMatchPosts(userIdx, town, startTime, endTime);
        }
        catch (Exception e){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}

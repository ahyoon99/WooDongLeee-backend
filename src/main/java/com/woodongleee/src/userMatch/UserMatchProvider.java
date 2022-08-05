package com.woodongleee.src.userMatch;

import com.woodongleee.config.BaseException;
import com.woodongleee.config.BaseResponseStatus;
import com.woodongleee.src.userMatch.model.GetUserMatchPostInfoRes;
import com.woodongleee.src.userMatch.model.UserMatchApplyInfo;
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

    public List<UserMatchApplyInfo> getUserMatchApplyList(int userIdx, int teamScheduleIdx) throws BaseException{
        try{
            if (userMatchDao.isLeader(userIdx) != 1) {
                throw new BaseException(BaseResponseStatus.ACCEPT_NOT_AVAILABLE); // 리더가 아닙니다.
            }

            if (userMatchDao.isOurMatch(userIdx, teamScheduleIdx) != 1) {
                throw new BaseException(BaseResponseStatus.ACCEPT_NOT_AVAILABLE); // teamScheduleIdx가 다른 팀의 것입니다.
            }

            if(userMatchDao.existsMatchPost(teamScheduleIdx) != 1){
                throw new BaseException(BaseResponseStatus.MATCHING_DOES_NOT_EXIST); // 용병 모집글이 작성되지 않음.
            }

            return userMatchDao.getUserMatchApplyList(teamScheduleIdx);
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}
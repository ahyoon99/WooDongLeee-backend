package com.woodongleee.src.teamMatch;

import com.woodongleee.config.BaseException;
import com.woodongleee.config.BaseResponseStatus;
import com.woodongleee.src.teamMatch.model.GetApplyTeamRes;
import com.woodongleee.src.teamMatch.model.GetTeamMatchPostRes;
import com.woodongleee.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamMatchProvider {

    private final TeamMatchDao teamMatchDao;
    private final JwtService jwtService;
    public static final int DO_NOT_EXIST = 0;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public TeamMatchProvider(TeamMatchDao teamMatchDao, JwtService jwtService) {
        this.teamMatchDao = teamMatchDao;
        this.jwtService = jwtService;
    }

    public int selectUserIdxByMatchPostIdx(int matchPostIdx) throws BaseException {
        if(doNotExistTeamMatchPostIdx(matchPostIdx)){
            throw new BaseException(BaseResponseStatus.MATCH_POST_DOES_NOT_EXIST);
        }

        int userIdx = teamMatchDao.findUserIdxByPostIdx(matchPostIdx);
        return userIdx;
    }
    
    public boolean doNotExistTeamMatchPostIdx(int matchPostIdx){
        return teamMatchDao.existTeamMatchPostIdx(matchPostIdx) == DO_NOT_EXIST;
    }

    public int selectHomeIdxByTeamScheduleIdx(int teamScheduleIdx) throws BaseException{
        if(doNotExistTeamMatch(teamScheduleIdx)){
            throw new BaseException(BaseResponseStatus.SCHEDULE_DOES_NOT_EXIST);
        }

        int homeTeamIdx = teamMatchDao.selectHomeIdxByTeamScheduleIdx(teamScheduleIdx);
        return homeTeamIdx;
    }

    public int selectAwayIdxByTeamScheduleIdx(int teamScheduleIdx) throws BaseException{
        if(doNotExistTeamMatch(teamScheduleIdx)){
            throw new BaseException(BaseResponseStatus.SCHEDULE_DOES_NOT_EXIST);
        }

        int awayTeamIdx = teamMatchDao.selectAwayIdxByTeamScheduleIdx(teamScheduleIdx);
        return awayTeamIdx;
    }

    public int selectLeaderIdxByTeamIdx(int homeIdx) throws BaseException{
        if(doNotExistTeamInfo(homeIdx)){
            throw new BaseException(BaseResponseStatus.TEAM_DOES_NOT_EXIST);
        }
        int leaderUserIdx = teamMatchDao.selectLeaderIdxByTeamIdx(homeIdx);
        return leaderUserIdx;
    }
    
    public boolean doNotExistTeamInfo(int teamIdx){
        return teamMatchDao.existTeamInfo(teamIdx)==DO_NOT_EXIST;
    }

    public List<GetApplyTeamRes> getApplyTeam(int teamScheduleIdx) throws BaseException{
        try{
            if(doNotExistTeamMatch(teamScheduleIdx)){
                throw new BaseException(BaseResponseStatus.SCHEDULE_DOES_NOT_EXIST);
            }

            List<GetApplyTeamRes> getApplyTeamResList = teamMatchDao.getApplyTeamRes(teamScheduleIdx);
            return getApplyTeamResList;
        } catch(BaseException e){
            throw e;
        }catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public int selectUserIdxByTeamScheduleIdx(int teamScheduleIdx) throws BaseException{
        try{
            if(doNotExistTeamMatch(teamScheduleIdx)){
                throw new BaseException(BaseResponseStatus.SCHEDULE_DOES_NOT_EXIST);
            }

            int userIdx = teamMatchDao.selectUserIdxByTeamScheduleIdx(teamScheduleIdx);
            return userIdx;
        }catch(BaseException e){
            throw e;
        }catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
    
    public int selectHomeLeaderIdxByMatchApplyIdx(int matchApplyIdx) throws BaseException{
        if(doNotExistMatchApplyIdx(matchApplyIdx)){
            throw new BaseException(BaseResponseStatus.MATCH_APPLY_DOES_NOT_EXIST);
        }

        int leaderUserIdx = teamMatchDao.selectHomeLeaderIdxByMatchApplyIdx(matchApplyIdx);
        return leaderUserIdx;
    }
    
    public boolean doNotExistMatchApplyIdx(int matchApplyIdx){
        return teamMatchDao.existMatchApplyIdx(matchApplyIdx)==DO_NOT_EXIST;
    }

    public List<GetTeamMatchPostRes> getTeamMatchPosts(int userIdxByJwt, String town, String startTime, String endTime) throws BaseException {
        try{
            if(checkUserStatus(userIdxByJwt)){
                throw new BaseException(BaseResponseStatus.LEAVED_USER);
            }
            if(doNotExistUser(userIdxByJwt)){
                throw new BaseException(BaseResponseStatus.USER_DOES_NOT_EXIST);
            }

            List<GetTeamMatchPostRes> getTeamMatchPostResList = teamMatchDao.getTeamMatchPosts(userIdxByJwt, town, startTime, endTime);
            return getTeamMatchPostResList;
        } catch(BaseException e){
            throw e;
        }catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
    
    public boolean checkUserStatus(int userIdx){
        return teamMatchDao.checkUserStatus(userIdx).equals("INACTIVE");
    }
    
    public boolean doNotExistUser(int userIdx){
        return teamMatchDao.checkUserExist(userIdx)==DO_NOT_EXIST;
    }
    
    public boolean doNotExistTeamMatch(int teamScheduleIdx){
        return teamMatchDao.existTeamMatch(teamScheduleIdx) == DO_NOT_EXIST;
    }

    public boolean checkUserIdxAndUserIdxByJWT(int userIdx, int userIdxByJwt){
        return userIdx == userIdxByJwt;
    }
}

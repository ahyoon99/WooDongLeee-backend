package com.woodongleee.src.teamMatch;

import com.woodongleee.config.BaseException;
import com.woodongleee.config.BaseResponse;
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
        // 팀 매칭 글이 존재하지 않습니다.
        if(existTeamMatchPostIdx(matchPostIdx)){    // 팀 매칭글이 존재하지 않는 경우
            throw new BaseException(BaseResponseStatus.MATCH_POST_DOES_NOT_EXIST);
        }

        int userIdx = teamMatchDao.findUserIdxByPostIdx(matchPostIdx); // 매칭글 idx로 매칭글 작성한 유저 idx 찾기
        return userIdx;
    }

    // 팀 매칭 글이 존재하지 않습니다.
    public boolean existTeamMatchPostIdx(int matchPostIdx){
        return teamMatchDao.existTeamMatchPostIdx(matchPostIdx) == DO_NOT_EXIST;
    }

    // teamScheduleIdx에서 homeIdx(팀 idx) 가져오기
    public int selectHomeIdxByTeamScheduleIdx(int teamScheduleIdx) throws BaseException{
        // 존재하지 않는 팀 일정(경기)입니다.
        if(existTeamMatch(teamScheduleIdx)){    // 탐 일정이 존재하지 않는 경우
            throw new BaseException(BaseResponseStatus.SCHEDULE_DOES_NOT_EXIST);
        }

        int homeTeamIdx = teamMatchDao.selectHomeIdxByTeamScheduleIdx(teamScheduleIdx);
        return homeTeamIdx;
    }

    // teamScheduleIdx에서 awayIdx(팀 idx) 가져오기
    public int selectAwayIdxByTeamScheduleIdx(int teamScheduleIdx) throws BaseException{
        // 존재하지 않는 팀 일정(경기)입니다.
        if(existTeamMatch(teamScheduleIdx)){    // 탐 일정이 존재하지 않는 경우
            throw new BaseException(BaseResponseStatus.SCHEDULE_DOES_NOT_EXIST);
        }

        int awayTeamIdx = teamMatchDao.selectAwayIdxByTeamScheduleIdx(teamScheduleIdx);
        return awayTeamIdx;
    }
    
    // 팀 매칭 신청 리스트 조회하기
    public List<GetApplyTeamRes> getApplyTeam(int teamScheduleIdx) throws BaseException{
        try{
            // 1. 존재하지 않는 팀 일정(경기)입니다.
            if(existTeamMatch(teamScheduleIdx)){    // 탐 일정이 존재하지 않는 경우
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

    // teamSchedule을 만든 userIdx 찾기
    public int selectUserIdxByTeamScheduleIdx(int teamScheduleIdx) throws BaseException{
        try{
            // 존재하지 않는 팀 일정(경기)입니다.
            if(existTeamMatch(teamScheduleIdx)){    // 탐 일정이 존재하지 않는 경우
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

    // 존재하지 않는 팀 일정(경기)입니다.
    public boolean existTeamMatch(int teamScheduleIdx){
        return teamMatchDao.existTeamMatch(teamScheduleIdx) == DO_NOT_EXIST;
    }
    
    public int selectHomeLeaderIdxByMatchApplyIdx(int matchApplyIdx) throws BaseException{
        // 팀 신청 내역이 존재하지 않습니다.
        if(existMatchApplyIdx(matchApplyIdx)){
            throw new BaseException(BaseResponseStatus.MATCH_APPLY_DOES_NOT_EXIST);
        }

        int leaderUserIdx = teamMatchDao.selectHomeLeaderIdxByMatchApplyIdx(matchApplyIdx);
        return leaderUserIdx;
    }

    // 팀 신청 내역이 존재하지 않습니다.
    public boolean existMatchApplyIdx(int matchApplyIdx){
        return teamMatchDao.existMatchApplyIdx(matchApplyIdx)==DO_NOT_EXIST;
    }

    // 팀의 리더 userIdx 가져오기
    public int selectLeaderIdxByTeamIdx(int homeIdx) throws BaseException{
        // 존재하지 않는 팀입니다.
        if(existTeamInfo(homeIdx)){     // 팀이 존재하지 않는 경우
            throw new BaseException(BaseResponseStatus.TEAM_DOES_NOT_EXIST);
        }

        int leaderUserIdx = teamMatchDao.selectLeaderIdxByTeamIdx(homeIdx);
        return leaderUserIdx;
    }

    // 존재하지 않는 팀입니다.
    public boolean existTeamInfo(int teamIdx){
        return teamMatchDao.existTeamInfo(teamIdx)==DO_NOT_EXIST;
    }
    
    // 팀 매칭글 조회하기
    public List<GetTeamMatchPostRes> getTeamMatchPosts(int userIdxByJwt, String town, String startTime, String endTime) throws BaseException {
        try{
            // 0. 탈퇴한 유저입니다.
            if(checkUserStatus(userIdxByJwt)){   // 사용자가 탈퇴한 회원인 경우
                throw new BaseException(BaseResponseStatus.LEAVED_USER);
            }
            // 1. 존재하지 않은 유저입니다.
            if(checkUserExist(userIdxByJwt)){
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

    // 탈퇴한 유저입니다.
    public boolean checkUserStatus(int userIdx){
        return teamMatchDao.checkUserStatus(userIdx).equals("INACTIVE");
    }

    // 존재하지 않은 유저입니다.
    public boolean checkUserExist(int userIdx){
        return teamMatchDao.checkUserExist(userIdx)==DO_NOT_EXIST;
    }


    public boolean checkUserIdxAndUserIdxByJWT(int userIdx, int userIdxByJwt){
        return userIdx == userIdxByJwt;
    }
}

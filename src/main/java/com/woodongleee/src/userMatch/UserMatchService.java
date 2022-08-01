package com.woodongleee.src.userMatch;

import com.woodongleee.config.BaseException;
import com.woodongleee.config.BaseResponse;
import com.woodongleee.config.BaseResponseStatus;
import com.woodongleee.src.userMatch.Domain.*;
import com.woodongleee.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

@Service
public class UserMatchService {
    private final UserMatchDao userMatchDao;
    private final JwtService jwtService;

    @Autowired
    public UserMatchService(UserMatchDao userMatchDao, JwtService jwtService){
        this.userMatchDao = userMatchDao;
        this.jwtService = jwtService;
    }
    public void applyUserMatch(int userIdx, int matchPostIdx) throws BaseException {
        try {
            if(userMatchDao.checkMatchPostExist(matchPostIdx) != 1){
                throw new BaseException(BaseResponseStatus.ACCEPT_NOT_AVAILABLE); // 존재하지 않는 matchPostIdx
            }

            CheckApplyingPossibilityRes checkApplyingPossibilityRes =  userMatchDao.checkApplyingPossibility(userIdx, matchPostIdx);
            int status = checkApplyingPossibilityRes.getStatus();
            int headCnt = checkApplyingPossibilityRes.getHeadCnt();
            int joinCnt = checkApplyingPossibilityRes.getJoinCnt();
            int userMatchCnt = checkApplyingPossibilityRes.getUserMatchCnt();
            int teamIdx = checkApplyingPossibilityRes.getTeamIdx();

            if(userMatchDao.getTeamIdx(userIdx) == teamIdx){
                throw new BaseException(BaseResponseStatus.ACCEPT_NOT_AVAILABLE); // 해당 경기의 참가 팀 소속입니다.
            }

            if(status == 1){
                throw new BaseException(BaseResponseStatus.MATCH_ALREADY_EXIST); // 이미 신청했습니다.
            }

            if((joinCnt + userMatchCnt) >= headCnt){
                throw new BaseException(BaseResponseStatus.ACCEPT_NOT_AVAILABLE); // 용병 모집이 완료된 경기입니다.
            }


            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String startTime = checkApplyingPossibilityRes.getStartTime();
            String curTime = format.format(new Date());
            Date _startTime = format.parse(startTime);
            Date _curTime = format.parse(curTime);
            long diff = _startTime.getTime() - _curTime.getTime();
            diff = (((diff / 1000) / 60) / 60);

            if(diff <= 2){
                throw new BaseException(BaseResponseStatus.MATCH_APPLY_PERIOD_ERROR); // 용병 모집 기한이 지난 경기입니다.
            }


            userMatchDao.applyUserMatch(userIdx, matchPostIdx);
        }
        catch (BaseException e){
            throw e;
        }
        catch (Exception e){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public void cancelApplyUserMatch(int userIdx, int matchPostIdx) throws BaseException{
        try{
            if(userMatchDao.checkMatchPostExist(matchPostIdx) != 1){
                throw new BaseException(BaseResponseStatus.ACCEPT_NOT_AVAILABLE); // 존재하지 않는 matchPostIdx
            }

            if(userMatchDao.checkMatchApplyExist(userIdx, matchPostIdx) != 1){
                throw new BaseException(BaseResponseStatus.MATCH_APPLY_DOES_NOT_EXIST); // 존재하지 않는 매칭신청입니다.
            }

            CheckCancelApplyingPossibilityRes checkCancelApplyingPossibilityRes = userMatchDao.checkCancelApplyingPossibility(userIdx, matchPostIdx);

            if(checkCancelApplyingPossibilityRes.getStatus().equals("CANCELED")){
                throw new BaseException(BaseResponseStatus.ACCEPT_NOT_AVAILABLE); // 이미 취소된 신청입니다.
            }


            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String startTime = checkCancelApplyingPossibilityRes.getStartTime();
            String curTime = format.format(new Date());
            Date _startTime = format.parse(startTime);
            Date _curTime = format.parse(curTime);
            long diff = _startTime.getTime() - _curTime.getTime();
            diff = (((diff / 1000) / 60) / 60);

            if(diff <= 2){
                throw new BaseException(BaseResponseStatus.MATCH_APPLY_PERIOD_ERROR); // 용병 모집 기한이 지난 경기입니다.
            }

            userMatchDao.cancelApplyUserMatch(userIdx, matchPostIdx);
        }
        catch (BaseException e){
            throw e;
        }
        catch (Exception e){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }


    public BaseResponse<CreateUserMatchPostRes> createUserMatchPost(int userIdx, int teamScheduleIdx, String contents) throws BaseException{
        try{
           if(userMatchDao.isLeader(userIdx) != 1){
               throw new BaseException(BaseResponseStatus.ACCEPT_NOT_AVAILABLE); // 리더가 아닙니다.
           }

           CheckCreateUserMatchPostPossibilityRes createUserMatchPostPossibilityRes = userMatchDao.checkCreateMatchPostPossibility(userIdx, teamScheduleIdx);
           if(createUserMatchPostPossibilityRes.getStatus() == 1) {
               throw new BaseException(BaseResponseStatus.MATCH_ALREADY_EXIST); // 이미 용병 모집글이 작성된 경기입니다.
           }

           if(userMatchDao.isOurMatch(userIdx, teamScheduleIdx) != 1){
               throw new BaseException(BaseResponseStatus.ACCEPT_NOT_AVAILABLE); // teamScheduleIdx가 다른 팀의 것입니다.
           }

           //우리팀 경기가 아닙니다 추가.. -> 잘못된 teamScheduleIdx
           SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
           String startTime = createUserMatchPostPossibilityRes.getStartTime();
           String curTime = format.format(new Date());
           Date _startTime = format.parse(startTime);
           Date _curTime = format.parse(curTime);
           long diff = _startTime.getTime() - _curTime.getTime();
           diff = (((diff / 1000) / 60) / 60);

           if(diff <= 2){
               throw new BaseException(BaseResponseStatus.MATCH_CREATE_PERIOD_ERROR); // 용병 모집글 작성 기한이 지났습니다.
           }

            int matchPostIdx = userMatchDao.createUserMatchPost(userIdx, teamScheduleIdx, contents);
            return new BaseResponse<>(new CreateUserMatchPostRes(matchPostIdx, "USER"));
        }
        catch (BaseException e){
            throw e;
        }
        catch (Exception e){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }


}

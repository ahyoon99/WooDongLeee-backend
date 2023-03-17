package com.woodongleee.src.teamMatch;

import com.woodongleee.config.BaseException;
import com.woodongleee.config.BaseResponse;
import com.woodongleee.config.BaseResponseStatus;
import com.woodongleee.src.teamMatch.model.*;
import com.woodongleee.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


@Service
public class TeamMatchService {

    public static final int DO_NOT_EXIST = 0;

    private final TeamMatchDao teamMatchDao;
    private final TeamMatchProvider teamMatchProvider;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    public TeamMatchService(TeamMatchDao teamMatchDao, TeamMatchProvider teamMatchProvider, JwtService jwtService) {
        this.teamMatchDao = teamMatchDao;
        this.teamMatchProvider = teamMatchProvider;
        this.jwtService = jwtService;
    }    
    
    public BaseResponse<PostTeamMatchPostsRes> createTeamMatchPost(int userIdx, PostTeamMatchPostsReq postTeamMatchPostsReq) throws BaseException{
        try{
            if(checkUserStatus(userIdx)){
                return new BaseResponse<>(BaseResponseStatus.LEAVED_USER);
            }
            if(isLeader(userIdx)){
                return new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED_ACCESS);
            }
            if(teamMatchDao.existTeamMatchPost(postTeamMatchPostsReq.getTeamScheduleIdx()) > DO_NOT_EXIST){
                return new BaseResponse<>(BaseResponseStatus.MATCH_ALREADY_EXIST);
            }
            if(doNotExistTeamMatch(postTeamMatchPostsReq.getTeamScheduleIdx())){
                return new BaseResponse<>(BaseResponseStatus.SCHEDULE_DOES_NOT_EXIST);
            }
            if(validPostAccessTime(postTeamMatchPostsReq.getTeamScheduleIdx())){
                return new BaseResponse<>(BaseResponseStatus.MATCH_CREATE_PERIOD_ERROR);
            }

            int postIdx = teamMatchDao.insertMatchPost(userIdx, postTeamMatchPostsReq);
            return new BaseResponse<>(new PostTeamMatchPostsRes(postIdx, "TEAM"));

        }catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public BaseResponse<ModifyTeamMatchPostsRes> modifyTeamMatchPost(ModifyTeamMatchPostsReq modifyTeamMatchPostsReq, int matchPostIdx) throws BaseException {
        try{
            if(checkUserStatus(modifyTeamMatchPostsReq.getUserIdx())){
                return new BaseResponse<>(BaseResponseStatus.LEAVED_USER);
            }
            if(isLeader(modifyTeamMatchPostsReq.getUserIdx())){
                return new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED_ACCESS);
            }
            if(doNotExistTeamMatchPostIdx(matchPostIdx)){
                return new BaseResponse<>(BaseResponseStatus.MATCH_POST_DOES_NOT_EXIST);
            }
            if(doNotExistTeamMatch(modifyTeamMatchPostsReq.getTeamScheduleIdx())){
                return new BaseResponse<>(BaseResponseStatus.SCHEDULE_DOES_NOT_EXIST);
            }
            if(!intEqualsInt(findUserIdxByPostIdx(matchPostIdx),modifyTeamMatchPostsReq.getUserIdx())){
                return new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED_POST);
            }
            if(validPostAccessTime(modifyTeamMatchPostsReq.getTeamScheduleIdx())){
                return new BaseResponse<>(BaseResponseStatus.MATCH_CREATE_PERIOD_ERROR);
            }

            int result = teamMatchDao.updateMatchPost(matchPostIdx, modifyTeamMatchPostsReq.getContents());
            if(intEqualsInt(result,0)) {
                throw new BaseException(BaseResponseStatus.MODIFY_FAIL_POST);
            }
            return new BaseResponse<>(new ModifyTeamMatchPostsRes(matchPostIdx,"TEAM"));

        }catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public int findUserIdxByPostIdx(int matchPostIdx){
        return teamMatchDao.findUserIdxByPostIdx(matchPostIdx);
    }

    public void deleteTeamMatchPosts(int userIdxByJwt, int matchPostIdx) throws BaseException {
        try{
            if(checkUserStatus(userIdxByJwt)){
                throw new BaseException(BaseResponseStatus.LEAVED_USER);
            }
            if(isLeader(userIdxByJwt)){
                throw new BaseException(BaseResponseStatus.UNAUTHORIZED_ACCESS);
            }
            if(validPostAccessTime(teamMatchDao.selectScheduleIdxByMatchPostIdx(matchPostIdx))){
                throw new BaseException(BaseResponseStatus.MATCH_CREATE_PERIOD_ERROR);
            }

            int result = teamMatchDao.deleteTeamMatchPosts(matchPostIdx);
            if(intEqualsInt(result,0)){
                throw new BaseException(BaseResponseStatus.DELETE_FAIL_POST);
            }
        }catch(BaseException e){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }


    
    public void applyTeamMatch(int userIdxByJwt, int matchPostIdx) throws BaseException{
        try{
            if(checkUserStatus(userIdxByJwt)){
                throw new BaseException(BaseResponseStatus.LEAVED_USER);
            }
            if(isLeader(userIdxByJwt)){
                throw new BaseException(BaseResponseStatus.UNAUTHORIZED_ACCESS);
            }
            if(doNotExistTeamMatchPostIdx(matchPostIdx)){
                throw new BaseException(BaseResponseStatus.MATCH_POST_DOES_NOT_EXIST);
            }
            if(checkApplyStatus(matchPostIdx)){
                throw new BaseException(BaseResponseStatus.ACCEPT_NOT_AVAILABLE);
            }
            if(validPostAccessTime(teamMatchDao.selectScheduleIdxByMatchPostIdx(matchPostIdx))){
                throw new BaseException(BaseResponseStatus.MATCH_CREATE_PERIOD_ERROR);
            }

            teamMatchDao.applyTeamMatch(userIdxByJwt, matchPostIdx);
        }catch(BaseException e){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
    
    public void cancelApplyTeamMatch(int userIdxByJwt, int matchPostIdx) throws BaseException {
        try {
            if(checkUserStatus(userIdxByJwt)){
                throw new BaseException(BaseResponseStatus.LEAVED_USER);
            }
            if(isLeader(userIdxByJwt)){
                throw new BaseException(BaseResponseStatus.UNAUTHORIZED_ACCESS);
            }
            if (doNotExistTeamMatchPostIdx(matchPostIdx)) {
                throw new BaseException(BaseResponseStatus.MATCH_POST_DOES_NOT_EXIST);
            }
            if(doNotExistMatchApply(userIdxByJwt, matchPostIdx)){
                throw new BaseException(BaseResponseStatus.TEAM_APPLY_DOES_NOT_EXIST);
            }
            if (checkMatchPostStatus(userIdxByJwt, matchPostIdx,"ACCEPTED")) {
                throw new BaseException(BaseResponseStatus.ACCEPT_NOT_AVAILABLE);
            }
            if (checkMatchPostStatus(userIdxByJwt, matchPostIdx,"CANCELED")) {
                throw new BaseException(BaseResponseStatus.ACCEPT_NOT_AVAILABLE);
            }
            if(validPostAccessTime(teamMatchDao.selectScheduleIdxByMatchPostIdx(matchPostIdx))){
                throw new BaseException(BaseResponseStatus.MATCH_CREATE_PERIOD_ERROR);
            }

            teamMatchDao.cancelApplyTeamMatch(userIdxByJwt, matchPostIdx);
        } catch (BaseException e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
    
    public boolean doNotExistMatchApply(int userIdx, int matchPostIdx){
        return teamMatchDao.existMatchApply(userIdx, matchPostIdx)==DO_NOT_EXIST;
    }
    
    public boolean checkMatchPostStatus(int userIdx, int matchPostIdx, String status){
        return teamMatchDao.checkAlreadyApplyStatus(userIdx, matchPostIdx).equals(status);
    }
    
    public BaseResponse<PostGameResultRes> postGameResult(PostGameResultReq postGameResultReq) throws BaseException{
        try{
            int homeTeamIdx = teamMatchProvider.selectHomeIdxByTeamScheduleIdx(postGameResultReq.getTeamScheduleIdx());
            int awayTeamIdx = teamMatchProvider.selectAwayIdxByTeamScheduleIdx(postGameResultReq.getTeamScheduleIdx());
            int leaderUserIdx = teamMatchProvider.selectLeaderIdxByTeamIdx(homeTeamIdx);

            if(checkUserStatus(leaderUserIdx)){
                return new BaseResponse<>(BaseResponseStatus.LEAVED_USER);
            }
            if(isLeader(leaderUserIdx)){
                return new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED_ACCESS);
            }
            if(doNotExistTeamMatch(postGameResultReq.getTeamScheduleIdx())){
                throw new BaseException(BaseResponseStatus.SCHEDULE_DOES_NOT_EXIST);
                //throw new BaseResponse<>(BaseResponseStatus.SCHEDULE_DOES_NOT_EXIST); 아닌가..?
            }
            if(validResultAccessTime(postGameResultReq.getTeamScheduleIdx())){
                return new BaseResponse<>(BaseResponseStatus.GAME_RESULT_PERIOD_ERROR);
            }

            int gameResultIdx = teamMatchDao.postGameResult(postGameResultReq);
            if(postGameResultReq.getAwayScore()>postGameResultReq.getHomeScore()){
                updateTeamScore(homeTeamIdx,1);
                updateTeamScore(awayTeamIdx,3);
            }
            else if(postGameResultReq.getAwayScore()<postGameResultReq.getHomeScore()){
                updateTeamScore(homeTeamIdx,3);
                updateTeamScore(awayTeamIdx,1);
            }
            else if(postGameResultReq.getAwayScore()==postGameResultReq.getHomeScore()){
                updateTeamScore(homeTeamIdx,2);
                updateTeamScore(awayTeamIdx,2);
            }
            return new BaseResponse<>(new PostGameResultRes(gameResultIdx));
        }catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public void updateTeamScore(int teamIdx, int plusScore){
        int teamScore = teamMatchDao.selectTeamScoreByTeamIdx(teamIdx);
        teamMatchDao.updateTeamScore(teamIdx, teamScore+plusScore);
    }

    public void rejectTeamMatchApply(int userIdxByJwt, int matchApplyIdx) throws BaseException{
        try{
            if(checkUserStatus(userIdxByJwt)){
                throw new BaseException(BaseResponseStatus.LEAVED_USER);
            }
            if(isLeader(userIdxByJwt)){
                throw new BaseException(BaseResponseStatus.UNAUTHORIZED_ACCESS);
            }
            if (doNotExistMatchApplyIdx(matchApplyIdx)) {
                throw new BaseException(BaseResponseStatus.MATCH_APPLY_DOES_NOT_EXIST);
            }
            if(checkMatchApplyIdxStatus(matchApplyIdx,"DENIED")){
                throw new BaseException(BaseResponseStatus.MATCH_APPLY_ALREADY_DENIED);
            }
            if(checkMatchApplyIdxStatus(matchApplyIdx,"CANCELED")){
                throw new BaseException(BaseResponseStatus.MATCH_APPLY_ALREADY_CANCELED);
            }

            teamMatchDao.rejectTeamMatchApply(matchApplyIdx);
        } catch (BaseException e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public void acceptTeamMatchApply(int userIdxByJwt, int matchApplyIdx) throws BaseException{
        try{
            if(checkUserStatus(userIdxByJwt)){
                throw new BaseException(BaseResponseStatus.LEAVED_USER);
            }
            if(isLeader(userIdxByJwt)){
                throw new BaseException(BaseResponseStatus.UNAUTHORIZED_ACCESS);
            }

            if (doNotExistMatchApplyIdx(matchApplyIdx)) {
                throw new BaseException(BaseResponseStatus.MATCH_APPLY_DOES_NOT_EXIST);
            }

            if(checkMatchApplyIdxStatus(matchApplyIdx,"ACCEPTED")){
                throw new BaseException(BaseResponseStatus.MATCH_APPLY_ALREADY_ACCEPTED);
            }
            
            int matchPostIdx = teamMatchDao.selectMatchPostIdxByMatchApplyIdx(matchApplyIdx);
            if(checkApplyStatus(matchPostIdx)){
                throw new BaseException(BaseResponseStatus.ACCEPT_NOT_AVAILABLE);
            }
            int teamScheduleIdx = teamMatchDao.selectScheduleIdxByMatchPostIdx(matchPostIdx);
            int awayLeaderUserIdx = teamMatchDao.selectAwayLeaderIdxByMatchApplyIdx(matchApplyIdx);
            int awayTeamIdx = teamMatchDao.selectTeamIdxByUserIdx(awayLeaderUserIdx);
            teamMatchDao.acceptTeamMatchApply(matchApplyIdx, matchPostIdx, teamScheduleIdx, awayTeamIdx);
        } catch (BaseException e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public boolean checkUserStatus(int userIdx){
        return teamMatchDao.checkUserStatus(userIdx).equals("INACTIVE");
    }

    public boolean isLeader(int userIdx){
        return teamMatchDao.isLeader(userIdx).equals("F");
    }

    public boolean existTeamMatch(int teamScheduleIdx){
        return teamMatchDao.existTeamMatch(teamScheduleIdx) == DO_NOT_EXIST;
    }
    
    public boolean doNotExistTeamMatch(int teamScheduleIdx){
        return teamMatchDao.existTeamMatch(teamScheduleIdx) == DO_NOT_EXIST;
    }
    
    public boolean doNotExistTeamMatchPostIdx(int matchPostIdx){
        return teamMatchDao.existTeamMatchPostIdx(matchPostIdx) == DO_NOT_EXIST;
    }
    
    public boolean doNotExistMatchApplyIdx(int matchApplyIdx){
        return teamMatchDao.existMatchApplyIdx(matchApplyIdx) == DO_NOT_EXIST;
    }

    public boolean checkApplyStatus(int matchPostIdx){
        return teamMatchDao.checkApplyStatus(matchPostIdx)> DO_NOT_EXIST;
    }

    public boolean checkMatchApplyIdxStatus(int matchApplyIdx, String status){
        return teamMatchDao.checkMatchApplyIdxStatus(matchApplyIdx).equals(status);
    }

    public boolean validPostAccessTime(int teamScheduleIdx){
        String start = selectStartTime(teamScheduleIdx);

        Date startDate = setDate(start);
        Calendar startTime = setCalendar(startDate);
        startTime = calTwoHoursAgo(startTime);  // post 접근 가능 시간 찾기

        Calendar now = setNowDate();
        return startTimeCompareToNow(startTime, now);
    }

    public String selectStartTime(int teamScheduleIdx){
        String start = "2000-01-01 00:00:00";
        try {
            start = teamMatchDao.selectStartTime(teamScheduleIdx);
            return start;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return start;
    }

    public Date setDate(String dateFormat){
        SimpleDateFormat formatedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try {
            date = formatedTime.parse(dateFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public Calendar setCalendar(Date date){
        Calendar time = Calendar.getInstance();
        time.setTime(date);
        return time;
    }

    // 경기 시작 2시간 전까지만 글 작성 가능하다.
    public Calendar calTwoHoursAgo(Calendar startTime){
        startTime.add(Calendar.HOUR, -1);
        startTime.add(Calendar.MINUTE, -59);
        return startTime;
    }

    public Calendar setNowDate(){
        Date date = new Date();     
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        return now;
    }

    public boolean startTimeCompareToNow(Calendar startTime, Calendar now){
        return now.compareTo(startTime)==-1;
    }

    // 경기가 종료된 후에 경기 결과 추가가 가능하다.
    public boolean validResultAccessTime(int teamScheduleIdx){
        String end = selectEndTime(teamScheduleIdx);

        Date endDate = setDate(end);
        Calendar endTime = setCalendar(endDate);

        Calendar now = setNowDate();   
        return nowCompareToEndTime(now, endTime);
    }

    public String selectEndTime(int teamScheduleIdx){
        String end = "9999-01-01 00:00:00";
        try {
            end = teamMatchDao.selectEndTime(teamScheduleIdx);
            return end;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return end;
    }

    public boolean nowCompareToEndTime(Calendar now, Calendar endTime){
        return endTime.compareTo(now)==-1;
    }

    public boolean intEqualsInt(int int1, int int2){
        return int1==int2;
    }
}

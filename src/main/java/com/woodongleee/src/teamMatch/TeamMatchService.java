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

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TeamMatchDao teamMatchDao;
    private final TeamMatchProvider teamMatchProvider;
    private final JwtService jwtService;
    public static final int DO_NOT_EXIST = 0;


    @Autowired
    public TeamMatchService(TeamMatchDao teamMatchDao, TeamMatchProvider teamMatchProvider, JwtService jwtService) {
        this.teamMatchDao = teamMatchDao;
        this.teamMatchProvider = teamMatchProvider;
        this.jwtService = jwtService;
    }

    // 탈퇴한 유저입니다.
    public boolean checkUserStatus(int userIdx){
        return teamMatchDao.checkUserStatus(userIdx).equals("INACTIVE");
    }

    // 팀 매칭글 생성은 리더만 가능합니다.
    public boolean isLeader(int userIdx){
        return teamMatchDao.isLeader(userIdx).equals("F");
    }

    // 존재하지 않는 팀 일정(경기)입니다.
    public boolean existTeamMatch(int teamScheduleIdx){
        return teamMatchDao.existTeamMatch(teamScheduleIdx) == DO_NOT_EXIST;
    }

    // 해당 팀 매칭글 작성자가 아닙니다.
    public int findUserIdxByPostIdx(int matchPostIdx){
        return teamMatchDao.findUserIdxByPostIdx(matchPostIdx);
    }

    // 두 int 변수가 같은지 확인
    public boolean intEqualsInt(int int1, int int2){
        return int1==int2;
    }

    // 존재하지 않는 팀 매칭 글입니다.
    public boolean existTeamMatchPostIdx(int matchPostIdx){
        return teamMatchDao.existTeamMatchPostIdx(matchPostIdx) == DO_NOT_EXIST;
    }

    // 팀 매칭 모집이 완료된 경기입니다.
    public boolean checkApplyStatus(int matchPostIdx){
        return teamMatchDao.checkApplyStatus(matchPostIdx)> DO_NOT_EXIST;
    }

    // 팀 매칭을 신청하지 않았습니다.
    public boolean existMatchApply(int userIdx, int matchPostIdx){
        return teamMatchDao.existMatchApply(userIdx, matchPostIdx)==DO_NOT_EXIST;
    }

    // 매칭 신청 상태 확인 EX) 이미 ACCEPTED된 매칭 신청입니다, 이미 취소한 팀 매칭입니다.
    public boolean checkAlreadyApplyStatus(int userIdx, int matchPostIdx, String status){
        return teamMatchDao.checkAlreadyApplyStatus(userIdx, matchPostIdx).equals(status);
    }

    // 존재하지 않는 팀 매칭 신청 내역입니다.
    public boolean existMatchApplyIdx(int matchApplyIdx){
        return teamMatchDao.existMatchApplyIdx(matchApplyIdx) == DO_NOT_EXIST;
    }

    // matchApply 상태 확인 EX) 이미 거절한 신청입니다.
    public boolean checkMatchApplyIdxStatus(int matchApplyIdx, String status){
        return teamMatchDao.checkMatchApplyIdxStatus(matchApplyIdx).equals(status);
    }

    // 팀 점수 update
    public void updateTeamScore(int teamIdx, int plusScore){
        int teamScore = teamMatchDao.selectTeamScoreByTeamIdx(teamIdx);
        teamMatchDao.updateTeamScore(teamIdx, teamScore+plusScore);
    }

    // 팀 매칭 글 작성 기한이 지났습니다. 경기 시작 2시간 전까지만 글 작성 가능.
    public boolean validPostAccessTime(int teamScheduleIdx){
        String start = selectStartTime(teamScheduleIdx);

        Date startDate = setDate(start);   // 경기 시작 시간 세팅하기
        Calendar startTime = setCalendar(startDate);
        startTime = calTwoHoursAgo(startTime);  // post 접근 가능 시간 찾기

        Calendar now = setNowDate();   // 현재 시간 세팅하기
        return startTimeCompareToNow(startTime, now);
    }

    public String selectStartTime(int teamScheduleIdx){
        String start = null;
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
            date = formatedTime.parse(dateFormat);     // 경기 시작 시간 세팅하기
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

    public Calendar calTwoHoursAgo(Calendar startTime){
        startTime.add(Calendar.HOUR, -1);
        startTime.add(Calendar.MINUTE, -59);
        return startTime;
    }

    public Calendar setNowDate(){
        Date date = new Date();     // 현재 시간 세팅하기
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        return now;
    }

    public boolean startTimeCompareToNow(Calendar startTime, Calendar now){
        return startTime.compareTo(now)==-1;
    }

    // 경기가 아직 종료되지 않았습니다. 경기가 끝나는 시간 부터 경기 결과 추가 가능.
    public boolean validResultAccessTime(int teamScheduleIdx){
        String end = selectEndTime(teamScheduleIdx);

        Date endDate = setDate(end);
        Calendar endTime = setCalendar(endDate);

        Calendar now = setNowDate();   // 현재 시간 세팅하기
        return nowCompareToEndTime(now, endTime);
    }

    public String selectEndTime(int teamScheduleIdx){
        String end = null;
        try {
            end = teamMatchDao.selectEndTime(teamScheduleIdx);
            return end;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return end;
    }

    public boolean nowCompareToEndTime(Calendar now, Calendar endTime){
        return now.compareTo(endTime)==-1;
    }

    // 팀 매칭 글 작성
    public BaseResponse<PostTeamMatchPostsRes> createTeamMatchPost(int userIdx, PostTeamMatchPostsReq postTeamMatchPostsReq) throws BaseException{
        try{
            // 0. 탈퇴한 유저입니다.
            if(checkUserStatus(userIdx)){   // 사용자가 탈퇴한 회원인 경우
                return new BaseResponse<>(BaseResponseStatus.LEAVED_USER);
            }

            // 1. 팀 매칭글 생성은 리더만 가능합니다.
            if(isLeader(userIdx)){    // 사용자가 리더가 아닌 경우
                return new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED_ACCESS);
            }

            // 2. 이미 팀 매칭 글 생성이 완료된 경기입니다.
            if(teamMatchDao.existTeamMatchPost(postTeamMatchPostsReq.getTeamScheduleIdx()) > DO_NOT_EXIST){    // 이미 팀 매칭글이 존재하는 경우
                return new BaseResponse<>(BaseResponseStatus.MATCH_ALREADY_EXIST);
            }

            // 3. 존재하지 않는 팀 일정(경기)입니다.
            if(existTeamMatch(postTeamMatchPostsReq.getTeamScheduleIdx())){    // 탐 일정이 존재하지 않는 경우
                return new BaseResponse<>(BaseResponseStatus.SCHEDULE_DOES_NOT_EXIST);
            }

            // 4. 팀 매칭 글 작성 기한이 지났습니다. 경기 시작 2시간 전까지만 글 작성 가능.
            if(validPostAccessTime(postTeamMatchPostsReq.getTeamScheduleIdx())){    // 팀 매칭 글 작성 기간이 지난 경우
                return new BaseResponse<>(BaseResponseStatus.MATCH_CREATE_PERIOD_ERROR);
            }

            // <<< 위의 조건들 모두 만족 시, 팀 매칭 게시글 작성 >>
            int postIdx = teamMatchDao.insertMatchPost(userIdx, postTeamMatchPostsReq);
            return new BaseResponse<>(new PostTeamMatchPostsRes(postIdx, "TEAM"));

        }catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public BaseResponse<ModifyTeamMatchPostsRes> modifyTeamMatchPost(ModifyTeamMatchPostsReq modifyTeamMatchPostsReq, int matchPostIdx) throws BaseException {
        try{
            // 0. 탈퇴한 유저입니다.
            if(checkUserStatus(modifyTeamMatchPostsReq.getUserIdx())){   // 사용자가 탈퇴한 회원인 경우
                return new BaseResponse<>(BaseResponseStatus.LEAVED_USER);
            }

            // 1. 팀 매칭글 수정은 리더만 가능합니다.
            if(isLeader(modifyTeamMatchPostsReq.getUserIdx())){  // 사용자가 리더가 아닌 경우
                return new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED_ACCESS);
            }

            // 2. 팀 매칭 글이 존재하지 않습니다.
            if(existTeamMatchPostIdx(matchPostIdx)){    // 팀 매칭글이 존재하지 않는 경우
                return new BaseResponse<>(BaseResponseStatus.MATCH_POST_DOES_NOT_EXIST);
            }

            // 3. 존재하지 않는 팀 일정(경기)입니다.
            if(existTeamMatch(modifyTeamMatchPostsReq.getTeamScheduleIdx())){    // 탐 일정이 존재하지 않는 경우
                return new BaseResponse<>(BaseResponseStatus.SCHEDULE_DOES_NOT_EXIST);
            }

            // 4. 해당 팀 매칭글 작성자가 아닙니다.
            if(!intEqualsInt(findUserIdxByPostIdx(matchPostIdx),modifyTeamMatchPostsReq.getUserIdx())){  // 팀 매칭글 작성자가 아닌 경우
                return new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED_POST);
            }

            // 5. 팀 매칭 글 수정 기한이 지났습니다. 경기 시작 2시간 전까지만 글 작성 가능.
            if(validPostAccessTime(modifyTeamMatchPostsReq.getTeamScheduleIdx())){    // 팀 매칭 글 작성 기간이 지난 경우
                return new BaseResponse<>(BaseResponseStatus.MATCH_CREATE_PERIOD_ERROR);
            }

            // <<< 위의 조건들 모두 만족 시, 팀 매칭 게시글 수정 >>
            int result = teamMatchDao.updateMatchPost(matchPostIdx, modifyTeamMatchPostsReq.getContents());
            if(intEqualsInt(result,0)) {
                throw new BaseException(BaseResponseStatus.MODIFY_FAIL_POST);
            }
            return new BaseResponse<>(new ModifyTeamMatchPostsRes(matchPostIdx,"TEAM"));

        }catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public void deleteTeamMatchPosts(int userIdxByJwt, int matchPostIdx) throws BaseException {
        try{
            // 0. 탈퇴한 유저입니다.
            if(checkUserStatus(userIdxByJwt)){   // 사용자가 탈퇴한 회원인 경우
                throw new BaseException(BaseResponseStatus.LEAVED_USER);
            }

            // 1. 팀 매칭글 삭제는 리더만 가능합니다.
            if(isLeader(userIdxByJwt)){  // 사용자가 리더가 아닌 경우
                throw new BaseException(BaseResponseStatus.UNAUTHORIZED_ACCESS);
            }

            // 2. 팀 매칭 글 삭제 기한이 지났습니다. 경기 시작 2시간 전까지만 글 삭제 가능.
            if(validPostAccessTime(teamMatchDao.selectScheduleIdxByMatchPostIdx(matchPostIdx))){    // 팀 매칭 글 작성 기간이 지난 경우
                throw new BaseException(BaseResponseStatus.MATCH_CREATE_PERIOD_ERROR);
            }

            // <<< 위의 조건들 모두 만족 시, 팀 매칭 게시글 삭제 >>
            int result = teamMatchDao.deleteTeamMatchPosts(matchPostIdx);
            if(intEqualsInt(result,0)){
                throw new BaseException(BaseResponseStatus.DELETE_FAIL_POST);
            }
        }catch(BaseException e){
            throw e;
        }catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
    
    public void applyTeamMatch(int userIdxByJwt, int matchPostIdx) throws BaseException{
        try{
            // 0. 탈퇴한 유저입니다.
            if(checkUserStatus(userIdxByJwt)){   // 사용자가 탈퇴한 회원인 경우
                throw new BaseException(BaseResponseStatus.LEAVED_USER);
            }

            // 1. 팀 매칭 신청은 리더만 가능합니다.
            if(isLeader(userIdxByJwt)){  // 사용자가 리더가 아닌 경우
                throw new BaseException(BaseResponseStatus.UNAUTHORIZED_ACCESS);
            }

            // 2. 존재하지 않는 팀 매칭 글입니다.
            if(existTeamMatchPostIdx(matchPostIdx)){    // 팀 매칭글이 존재하지 않는 경우
                throw new BaseException(BaseResponseStatus.MATCH_POST_DOES_NOT_EXIST);
            }

            // 3. 팀 매칭 모집이 완료된 경기입니다.
            if(checkApplyStatus(matchPostIdx)){     // 이미 팀 매칭이 완료된 경우
                throw new BaseException(BaseResponseStatus.ACCEPT_NOT_AVAILABLE);
            }

            // 4. 팀 매칭 신청 기한이 지났습니다. 경기 시작 2시간 전까지만 팀 매칭 가능.
            if(validPostAccessTime(teamMatchDao.selectScheduleIdxByMatchPostIdx(matchPostIdx))){    // 팀 매칭 글 작성 기간이 지난 경우
                throw new BaseException(BaseResponseStatus.MATCH_CREATE_PERIOD_ERROR);
            }

            // <<< 위의 조건들 모두 만족 시, 팀 매칭 신청 >>
            teamMatchDao.applyTeamMatch(userIdxByJwt, matchPostIdx);
        }catch(BaseException e){
            throw e;
        }catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
    
    public void cancelApplyTeamMatch(int userIdxByJwt, int matchPostIdx) throws BaseException {
        try {
            // 0. 탈퇴한 유저입니다.
            if(checkUserStatus(userIdxByJwt)){   // 사용자가 탈퇴한 회원인 경우
                throw new BaseException(BaseResponseStatus.LEAVED_USER);
            }

            // 1. 팀 매칭 신청은 리더만 가능합니다.
            if(isLeader(userIdxByJwt)){  // 사용자가 리더가 아닌 경우
                throw new BaseException(BaseResponseStatus.UNAUTHORIZED_ACCESS);
            }

            // 2. 존재하지 않는 팀 매칭 글입니다.
            if (existTeamMatchPostIdx(matchPostIdx)) {    // 팀 매칭글이 존재하지 않는 경우
                throw new BaseException(BaseResponseStatus.MATCH_POST_DOES_NOT_EXIST);
            }

            // 3. 팀 매칭을 신청하지 않았습니다.
            if(existMatchApply(userIdxByJwt, matchPostIdx)){
                throw new BaseException(BaseResponseStatus.TEAM_APPLY_DOES_NOT_EXIST);
            }

            // 4. 이미 ACCEPTED된 매칭 신청입니다.
            if (checkAlreadyApplyStatus(userIdxByJwt, matchPostIdx,"ACCEPTED")) {   // 이미 ACCEPTED 된 신청인 경우
                throw new BaseException(BaseResponseStatus.ACCEPT_NOT_AVAILABLE);
            }

            // 5. 이미 취소한 팀 매칭입니다.
            if (checkAlreadyApplyStatus(userIdxByJwt, matchPostIdx,"CANCELED")) {   // 이미 CANCELED 된 신청인 경우
                throw new BaseException(BaseResponseStatus.ACCEPT_NOT_AVAILABLE);
            }

            // 6. 팀 매칭 신청 취소 기한이 지났습니다. 경기 시작 2시간 전까지만 팀 매칭 취소 가능.
            if(validPostAccessTime(teamMatchDao.selectScheduleIdxByMatchPostIdx(matchPostIdx))){    // 팀 매칭 글 작성 기간이 지난 경우
                throw new BaseException(BaseResponseStatus.MATCH_CREATE_PERIOD_ERROR);
            }

            // <<< 위의 조건들 모두 만족 시, 팀 매칭 신청 취소 >>
            teamMatchDao.cancelApplyTeamMatch(userIdxByJwt, matchPostIdx);
        } catch (BaseException e) {
            throw e;
        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
    
    public BaseResponse<PostGameResultRes> postGameResult(PostGameResultReq postGameResultReq) throws BaseException{
        try{
            // teamScheduleIdx에서 homeIdx(팀 idx), awayIdx(팀 idx) 가져오기
            int homeTeamIdx = teamMatchProvider.selectHomeIdxByTeamScheduleIdx(postGameResultReq.getTeamScheduleIdx());
            int awayTeamIdx = teamMatchProvider.selectAwayIdxByTeamScheduleIdx(postGameResultReq.getTeamScheduleIdx());

            // homeIdx를 가지는 리더 userIdx 가져오기
            int leaderUserIdx = teamMatchProvider.selectLeaderIdxByTeamIdx(homeTeamIdx);

            // 0. 탈퇴한 유저입니다.
            if(checkUserStatus(leaderUserIdx)){   // 사용자가 탈퇴한 회원인 경우
                return new BaseResponse<>(BaseResponseStatus.LEAVED_USER);
            }

            // 1. 경기 결과 추가는 리더만 가능합니다.
            if(isLeader(leaderUserIdx)){     // 사용자가 리더가 아닌 경우
                return new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED_ACCESS);
            }

            // 2. 존재하지 않는 팀 일정(경기)입니다.
            if(existTeamMatch(postGameResultReq.getTeamScheduleIdx())){    // 탐 일정이 존재하지 않는 경우
                throw new BaseException(BaseResponseStatus.SCHEDULE_DOES_NOT_EXIST);
            }

            // 3. 경기가 아직 종료되지 않았습니다. 경기가 끝나는 시간 부터 경기 결과 추가 가능.
            if(validResultAccessTime(postGameResultReq.getTeamScheduleIdx())){    // 팀 매칭 글 작성 기간이 지난 경우
                return new BaseResponse<>(BaseResponseStatus.GAME_RESULT_PERIOD_ERROR);
            }

            // <<< 위의 조건들 모두 만족 시, 경기 결과 추가 >>
            // 1. GameResult 테이블에 데이터 추가
            int gameResultIdx = teamMatchDao.postGameResult(postGameResultReq);

            // 2. TeamInfo 테이블의 teamScore 변경
            if(postGameResultReq.getAwayScore()>postGameResultReq.getHomeScore()){  // 홈 팀이 졌을 때
                updateTeamScore(homeTeamIdx,1);
                updateTeamScore(awayTeamIdx,3);
            }
            else if(postGameResultReq.getAwayScore()<postGameResultReq.getHomeScore()){     // 홈 팀이 이겼을 때
                updateTeamScore(homeTeamIdx,3);
                updateTeamScore(awayTeamIdx,1);
            }
            else if(postGameResultReq.getAwayScore()==postGameResultReq.getHomeScore()){     // 비겼을 때
                updateTeamScore(homeTeamIdx,2);
                updateTeamScore(awayTeamIdx,2);
            }
            return new BaseResponse<>(new PostGameResultRes(gameResultIdx));
        }catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public void rejectTeamMatchApply(int userIdxByJwt, int matchApplyIdx) throws BaseException{
        try{
            // 0. 탈퇴한 유저입니다.
            if(checkUserStatus(userIdxByJwt)){   // 사용자가 탈퇴한 회원인 경우
                throw new BaseException(BaseResponseStatus.LEAVED_USER);
            }

            // 1. 팀 매칭 신청은 리더만 가능합니다.
            if(isLeader(userIdxByJwt)){  // 사용자가 리더가 아닌 경우
                throw new BaseException(BaseResponseStatus.UNAUTHORIZED_ACCESS);
            }

            // 2. 존재하지 않는 팀 매칭 신청 내역입니다.
            if (existMatchApplyIdx(matchApplyIdx)) {    // 팀 매칭 신청이 존재하지 않는 경우
                throw new BaseException(BaseResponseStatus.MATCH_APPLY_DOES_NOT_EXIST);
            }

            // 3. 이미 거절한 신청입니다.
            if(checkMatchApplyIdxStatus(matchApplyIdx,"DENIED")){
                throw new BaseException(BaseResponseStatus.MATCH_APPLY_ALREADY_DENIED);
            }

            // 4. 취소된 신청입니다.
            if(checkMatchApplyIdxStatus(matchApplyIdx,"CANCELED")){
                throw new BaseException(BaseResponseStatus.MATCH_APPLY_ALREADY_CANCELED);
            }

            // <<< 위의 조건들 모두 만족 시, 팀 매칭 신청 거절 >>
            teamMatchDao.rejectTeamMatchApply(matchApplyIdx);
        } catch (BaseException e) {
            throw e;
        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
    
    // 팀 매칭 신청 승인하기
    public void acceptTeamMatchApply(int userIdxByJwt, int matchApplyIdx) throws BaseException{
        try{
            // 0. 탈퇴한 유저입니다.
            if(checkUserStatus(userIdxByJwt)){   // 사용자가 탈퇴한 회원인 경우
                throw new BaseException(BaseResponseStatus.LEAVED_USER);
            }

            // 1. 팀 매칭 신청은 리더만 가능합니다.
            if(isLeader(userIdxByJwt)){  // 사용자가 리더가 아닌 경우
                throw new BaseException(BaseResponseStatus.UNAUTHORIZED_ACCESS);
            }

            // 2. 존재하지 않는 팀 매칭 신청 내역입니다.
            if (existMatchApplyIdx(matchApplyIdx)) {    // 팀 매칭 신청이 존재하지 않는 경우
                throw new BaseException(BaseResponseStatus.MATCH_APPLY_DOES_NOT_EXIST);
            }

            // 4. 이미 승인한 신청입니다.
            if(checkMatchApplyIdxStatus(matchApplyIdx,"ACCEPTED")){
                throw new BaseException(BaseResponseStatus.MATCH_APPLY_ALREADY_ACCEPTED);
            }

            // 3. 팀 매칭이 이미 완료되었습니다.
            int matchPostIdx = teamMatchDao.selectMatchPostIdxByMatchApplyIdx(matchApplyIdx);
            if(checkApplyStatus(matchPostIdx)){ // 이미 팀 매칭이 완료된 경우
                throw new BaseException(BaseResponseStatus.ACCEPT_NOT_AVAILABLE);
            }

            // <<< 위의 조건들 모두 만족 시, 팀 매칭 신청 승인 >>
            // 팀 일정 Idx 가져오기
            int teamScheduleIdx = teamMatchDao.selectScheduleIdxByMatchPostIdx(matchPostIdx);

            // MatchApply 작성한 userIdx(away팀의 리더 Idx) 가져오기
            int awayLeaderUserIdx = teamMatchDao.selectAwayLeaderIdxByMatchApplyIdx(matchApplyIdx);

            // away팀의 Idx 가져오기
            int awayTeamIdx = teamMatchDao.selectTeamIdxByUserIdx(awayLeaderUserIdx);

            // 팀 매칭 신청 승인하기
            teamMatchDao.acceptTeamMatchApply(matchApplyIdx, matchPostIdx, teamScheduleIdx, awayTeamIdx);
        } catch (BaseException e) {
            throw e;
        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}

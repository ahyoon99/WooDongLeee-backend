package com.woodongleee.src.teams;

import com.woodongleee.config.BaseException;
import com.woodongleee.config.BaseResponse;
import com.woodongleee.config.BaseResponseStatus;
import com.woodongleee.src.teams.model.ModifyTeamInfoReq;
import com.woodongleee.src.teams.model.PostTeamReq;
import com.woodongleee.src.teams.model.VotePossibilityRes;
import com.woodongleee.src.user.UserProvider;
import com.woodongleee.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;


@Service
public class TeamsService {

    private final TeamsDao teamsDao;
    private final TeamsProvider teamsProvider;
    private final JwtService jwtService;
    private final UserProvider userProvider;

    @Autowired
    public TeamsService(TeamsDao teamsDao, TeamsProvider teamsProvider, JwtService jwtService, UserProvider userProvider){
        this.teamsDao=teamsDao;
        this.teamsProvider=teamsProvider;
        this.jwtService=jwtService;
        this.userProvider=userProvider;
    }

    public void teamApply(int userIdx, int teamIdx) throws BaseException {
        if(userProvider.checkUserExist(userIdx)==0){
            throw new BaseException(BaseResponseStatus.USER_DOES_NOT_EXIST);
        }
        if(userProvider.checkUserStatus(userIdx).equals("INACTIVE")){
            throw new BaseException(BaseResponseStatus.LEAVED_USER);
        }
        if (teamsDao.checkTeamIdxExist(teamIdx) != 1) {
            throw new BaseException(BaseResponseStatus.TEAM_DOES_NOT_EXIST);//존재하지 않는 팀 id
        }
        if (teamsDao.checkApply(teamIdx) != 1) {
            throw new BaseException(BaseResponseStatus.TEAM_NOT_RECRUITING); // 모집하지 않는 팀
        }
        if (teamsDao.checkTeamExist(userIdx)!=-1) {
            throw new BaseException(BaseResponseStatus.TEAM_ALREADY_EXIST); // 이미 가입된 팀 존재
        }
        try {
            teamsDao.teamApply(userIdx, teamIdx);
        }catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
    public void cancelTeamApply(int userIdx, int teamIdx) throws BaseException{
        if(userProvider.checkUserExist(userIdx)==0){
            throw new BaseException(BaseResponseStatus.USER_DOES_NOT_EXIST);
        }
        if(userProvider.checkUserStatus(userIdx).equals("INACTIVE")){
            throw new BaseException(BaseResponseStatus.LEAVED_USER);
        }
        if(teamsDao.checkTeamIdxExist(teamIdx)!=1){
            throw new BaseException(BaseResponseStatus.TEAM_DOES_NOT_EXIST); // 존재하지 않는 팀 id
        }
        if(teamsDao.checkStatus(userIdx, teamIdx)==1){
            throw new BaseException(BaseResponseStatus.CANCEL_NOT_AVAILABLE); // 취소를 할 수 없음
        }
        if(teamsDao.checkApplyExist(userIdx, teamIdx)!=1){
            throw new BaseException(BaseResponseStatus.TEAM_APPLY_DOES_NOT_EXIST); //신청 내역이 없습니다
        }
        try{
            teamsDao.cancelTeamApply(userIdx, teamIdx);
        }catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
    public void leaveTeam(int userIdx, int teamIdx) throws BaseException{
        if(userProvider.checkUserExist(userIdx)==0){
            throw new BaseException(BaseResponseStatus.USER_DOES_NOT_EXIST);
        }
        if(userProvider.checkUserStatus(userIdx).equals("INACTIVE")){
            throw new BaseException(BaseResponseStatus.LEAVED_USER);
        }
        if(teamsDao.checkTeamIdxExist(teamIdx)!=1){
            throw new BaseException(BaseResponseStatus.TEAM_DOES_NOT_EXIST); //존재하지 않는 팀 id
        }
        if(teamsDao.checkTeamExist(userIdx)!=teamIdx){
            throw new BaseException(BaseResponseStatus.NOT_TEAMMATE); //팀원이 아닙니다
        }
        try{
            teamsDao.leaveTeam(userIdx, teamIdx);
        }catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
    public void createTeam(int userIdx, PostTeamReq newTeam) throws BaseException{
        if(userProvider.checkUserExist(userIdx)==0){
            throw new BaseException(BaseResponseStatus.USER_DOES_NOT_EXIST);
        }
        if(userProvider.checkUserStatus(userIdx).equals("INACTIVE")){
            throw new BaseException(BaseResponseStatus.LEAVED_USER);
        }
        if(teamsProvider.isNameDuplicated(userIdx, newTeam.getName())){
            throw new BaseException(BaseResponseStatus.DUPLICATED_TEAM_NAME); //중복된 팀 이름
        }
        if(teamsDao.checkTeamExist(userIdx)!=-1){
            throw new BaseException(BaseResponseStatus.TEAM_ALREADY_EXIST);
        }
        try{
            teamsDao.createTeam(userIdx, newTeam);
        }catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
    public void modifyTeamInfo(int userIdx, int teamIdx, ModifyTeamInfoReq modifyTeam) throws BaseException{
        if(userProvider.checkUserExist(userIdx)==0){
            throw new BaseException(BaseResponseStatus.USER_DOES_NOT_EXIST);
        }
        if(userProvider.checkUserStatus(userIdx).equals("INACTIVE")){
            throw new BaseException(BaseResponseStatus.LEAVED_USER);
        }
        if(teamsDao.isLeader(userIdx)!=1){
            throw new BaseException(BaseResponseStatus.UNAUTHORIZED_POST); //글 수정은 리더만 가능
        }
        if(teamsDao.checkTeamIdxExist(teamIdx)!=1){
            throw new BaseException(BaseResponseStatus.TEAM_DOES_NOT_EXIST);
        }
        if(teamsDao.checkTeamExist(userIdx)!=teamIdx){
            throw new BaseException(BaseResponseStatus.ACCEPT_NOT_AVAILABLE); // 본인이 리더가 아닌 팀의 정보를 바꾸려고 할 떄
        }
        try{
            teamsDao.modifyTeamInfo(teamIdx, modifyTeam);
        }catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
    public void vote(int userIdx, int teamIdx, int teamScheduleIdx) throws BaseException{
        if(userProvider.checkUserExist(userIdx)==0){
            throw new BaseException(BaseResponseStatus.USER_DOES_NOT_EXIST);
        }
        if(userProvider.checkUserStatus(userIdx).equals("INACTIVE")){
            throw new BaseException(BaseResponseStatus.LEAVED_USER);
        }
        if(teamsDao.checkTeamIdxExist(teamIdx)!=1){
            throw new BaseException(BaseResponseStatus.TEAM_DOES_NOT_EXIST);
        }
        if(teamsDao.checkTeamScheduleIdxExist(teamScheduleIdx)!=1){
            throw new BaseException(BaseResponseStatus.SCHEDULE_DOES_NOT_EXIST); //존재하지 않는 팀 스케줄
        }
        if(teamsDao.checkTeamExist(userIdx)!=teamIdx){
            throw new BaseException(BaseResponseStatus.ACCEPT_NOT_AVAILABLE); // 본인의 팀 일정이 아닌 일정에 투표할 경우
        }
        try{
            //VotePossibilityRes votePossibilityRes=teamsDao.votePossibilityRes(teamScheduleIdx);
            //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //String startTime = votePossibilityRes.getStartTime();
            //String curTime = format.format(new Date());
            //Date _startTime = format.parse(startTime);
            //Date _curTime = format.parse(curTime);
            //long diff = _startTime.getTime() - _curTime.getTime();
            //diff = (((diff / 1000) / 60) / 60);

            //if (diff <= 2) {
            //    throw new BaseException(MATCH_APPLY_VOTE_PERIOD_ERROR); // 용병 모집 기한이 지난 경기입니다.
           // }
            teamsDao.vote(userIdx, teamIdx, teamScheduleIdx);
        }catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
    public void cancelVote(int userIdx, int teamIdx, int teamScheduleIdx) throws BaseException{
        if(userProvider.checkUserExist(userIdx)==0){
            throw new BaseException(BaseResponseStatus.USER_DOES_NOT_EXIST);
        }
        if(userProvider.checkUserStatus(userIdx).equals("INACTIVE")){
            throw new BaseException(BaseResponseStatus.LEAVED_USER);
        }
        if(teamsDao.checkTeamIdxExist(teamIdx)!=1){
            throw new BaseException(BaseResponseStatus.TEAM_DOES_NOT_EXIST);
        }
        if(teamsDao.checkTeamScheduleIdxExist(teamScheduleIdx)!=1){
            throw new BaseException(BaseResponseStatus.SCHEDULE_DOES_NOT_EXIST); //존재하지 않는 팀 스케줄
        }
        if(teamsDao.checkTeamExist(userIdx)!=teamIdx){
            throw new BaseException(BaseResponseStatus.ACCEPT_NOT_AVAILABLE); // 본인의 팀 일정이 아닌 일정에 투표할 경우
        }
        if(teamsDao.checkDeletion(userIdx, teamScheduleIdx)!=1){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR); //이미 유저스케줄에서 삭제되었을떄
        }
        try{
            //VotePossibilityRes votePossibilityRes=teamsDao.votePossibilityRes(teamScheduleIdx);
            //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //String startTime = votePossibilityRes.getStartTime();
            //String curTime = format.format(new Date());
            //Date _startTime = format.parse(startTime);
            //Date _curTime = format.parse(curTime);
            //long diff = _startTime.getTime() - _curTime.getTime();
            //diff = (((diff / 1000) / 60) / 60);

            //if (diff <= 2) {
            //    throw new BaseException(MATCH_APPLY_VOTE_PERIOD_ERROR); // 용병 모집 기한이 지난 경기입니다.
            // }
            teamsDao.cancelVote(userIdx, teamIdx, teamScheduleIdx);
        }catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }

    }

}

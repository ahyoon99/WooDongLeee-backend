package com.woodongleee.src.teams;

import com.woodongleee.config.BaseException;
import com.woodongleee.config.BaseResponseStatus;
import com.woodongleee.src.teams.model.*;
import com.woodongleee.src.user.UserProvider;
import com.woodongleee.utils.JwtService;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class TeamsProvider {
    private final TeamsDao teamsDao;
    private final JwtService jwtService;
    private final UserProvider userProvider;

    @Autowired
    public TeamsProvider(TeamsDao teamsDao, JwtService jwtService, UserProvider userProvider){
        this.teamsDao=teamsDao;
        this.jwtService=jwtService;
        this.userProvider=userProvider;
    }

    public List<GetTeamsinfoRes> getTeamsByTown(int userIdx, String town) throws BaseException{
        if(userProvider.checkUserExist(userIdx)==0){
            throw new BaseException(BaseResponseStatus.USER_DOES_NOT_EXIST);
        }
        if(userProvider.checkUserStatus(userIdx).equals("INACTIVE")){
            throw new BaseException(BaseResponseStatus.LEAVED_USER);
        }
        try{
            List<GetTeamsinfoRes> getTeamsinfoRes=teamsDao.getTeaminfoByTown(town);
            return getTeamsinfoRes;
        }catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetTeamsinfoRes getTeamsByName(int userIdx, String name) throws BaseException{
        if(userProvider.checkUserExist(userIdx)==0){
            throw new BaseException(BaseResponseStatus.USER_DOES_NOT_EXIST);
        }
        if(userProvider.checkUserStatus(userIdx).equals("INACTIVE")){
            throw new BaseException(BaseResponseStatus.LEAVED_USER);
        }
        if(teamsDao.checkTeamNameExist(name)!=1){
            throw new BaseException(BaseResponseStatus.TEAM_DOES_NOT_EXIST); //존재하지 않는 팀입니다.
        }
        try{
            GetTeamsinfoRes getTeamsinfoRes=teamsDao.getTeaminfoByName(name);
            return getTeamsinfoRes;
        }catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetTeamsinfoRes getTeaminfo(int userIdx, int teamIdx) throws BaseException{
        if(userProvider.checkUserExist(userIdx)==0){
            throw new BaseException(BaseResponseStatus.USER_DOES_NOT_EXIST);
        }
        if(userProvider.checkUserStatus(userIdx).equals("INACTIVE")){
            throw new BaseException(BaseResponseStatus.LEAVED_USER);
        }
        if(teamsDao.checkTeamIdxExist(teamIdx)!=1){
            throw new BaseException(BaseResponseStatus.TEAM_DOES_NOT_EXIST); //존재하지 않는 팀Idx입니다.
        }
        try{
            GetTeamsinfoRes getTeamsinfoRes=teamsDao.getTeaminfo(teamIdx);
            return getTeamsinfoRes;
        }catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }

    }

    public List<GetTeamsScheduleRes> getTeamsScheduleRes(int userIdx, int teamIdx, String startTime, String endTime) throws BaseException{
        if(userProvider.checkUserExist(userIdx)==0){
            throw new BaseException(BaseResponseStatus.USER_DOES_NOT_EXIST);
        }
        if(userProvider.checkUserStatus(userIdx).equals("INACTIVE")){
            throw new BaseException(BaseResponseStatus.LEAVED_USER);
        }
        if(teamsDao.checkTeamIdxExist(teamIdx)!=1){
            throw new BaseException(BaseResponseStatus.TEAM_DOES_NOT_EXIST);
        }
        try{
            List<GetTeamsScheduleRes> getTeamsScheduleRes=teamsDao.getTeamsScheduleRes(teamIdx, startTime, endTime);
            return getTeamsScheduleRes;
        }catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
    public GetTeamScheduleInfoRes getTeamScheduleInfoRes(int userIdx, int teamScheduleIdx) throws BaseException{
        if(userProvider.checkUserExist(userIdx)==0){
            throw new BaseException(BaseResponseStatus.USER_DOES_NOT_EXIST);
        }
        if(userProvider.checkUserStatus(userIdx).equals("INACTIVE")){
            throw new BaseException(BaseResponseStatus.LEAVED_USER);
        }
        if(teamsDao.checkTeamScheduleIdxExist(teamScheduleIdx)!=1){
            throw new BaseException(BaseResponseStatus.SCHEDULE_DOES_NOT_EXIST); //존재하지 않는 팀 스케줄
        }
        try{
            GetTeamScheduleInfoRes getTeamScheduleInfoRes=teamsDao.getTeamScheduleInfoRes(teamScheduleIdx);
            return getTeamScheduleInfoRes;
        }catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public List<GetUserInfoRes> getUserInfoRes(int userIdx, int teamIdx) throws BaseException{
        if(userProvider.checkUserExist(userIdx)==0){
            throw new BaseException(BaseResponseStatus.USER_DOES_NOT_EXIST);
        }
        if(userProvider.checkUserStatus(userIdx).equals("INACTIVE")){
            throw new BaseException(BaseResponseStatus.LEAVED_USER);
        }
        if(teamsDao.checkTeamIdxExist(teamIdx)!=1){
            throw new BaseException(BaseResponseStatus.TEAM_DOES_NOT_EXIST); //존재하지 않는 팀 id
        }
        try{
            List<GetUserInfoRes> getUserInfoRes=teamsDao.getUserInfoRes(teamIdx);
            return getUserInfoRes;
        }catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public List<GetTeamsinfoRes> getTeamsbyRank(int userIdx, String town) throws BaseException{
        if(userProvider.checkUserExist(userIdx)==0){
            throw new BaseException(BaseResponseStatus.USER_DOES_NOT_EXIST);
        }
        if(userProvider.checkUserStatus(userIdx).equals("INACTIVE")){
            throw new BaseException(BaseResponseStatus.LEAVED_USER);
        }
        try{
            List<GetTeamsinfoRes> getTeamsbyRankRes=teamsDao.getTeamsByRankRes(town);
            return getTeamsbyRankRes;
        }catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public boolean isNameDuplicated(int userIdx, String name) throws BaseException{
        if(userProvider.checkUserExist(userIdx)==0){
            throw new BaseException(BaseResponseStatus.USER_DOES_NOT_EXIST);
        }
        if(userProvider.checkUserStatus(userIdx).equals("INACTIVE")){
            throw new BaseException(BaseResponseStatus.LEAVED_USER);
        }
        if(teamsDao.checkTeamExist(userIdx)!=-1){
            throw new BaseException(BaseResponseStatus.TEAM_ALREADY_EXIST); //팀 없는 회원만 가능
        }
        try{
            return teamsDao.isNameDuplicated(name)==1;
        }catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

}

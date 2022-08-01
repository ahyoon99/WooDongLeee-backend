package com.woodongleee.src.teams;

import com.woodongleee.config.BaseException;
import com.woodongleee.config.BaseResponseStatus;
import com.woodongleee.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeamsService {

    private final TeamsDao teamsDao;
    private final TeamsProvider teamsProvider;
    private final JwtService jwtService;

    @Autowired
    public TeamsService(TeamsDao teamsDao, TeamsProvider teamsProvider, JwtService jwtService){
        this.teamsDao=teamsDao;
        this.teamsProvider=teamsProvider;
        this.jwtService=jwtService;
    }

    public void getTeaminfo(int userIdx, int teamIdx) throws BaseException{
        try{
            if(teamsDao.checkTeamIdxExist(teamIdx)!=1){
                throw new BaseException(BaseResponseStatus.TEAM_DOES_NOT_EXIST); //존재하지 않는 teamIdx
            }
        }catch(BaseException e){
            throw e;
        }catch(Exception e){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public void getTeamSchedule(int userIdx, int teamIdx, String startTime, String endTime) throws BaseException{
        try{
            if(teamsDao.checkTeamIdxExist(teamIdx)!=1){
                throw new BaseException(BaseResponseStatus.TEAM_DOES_NOT_EXIST); //존재하지 않는 teamIdx;
            }
        }catch(BaseException e){
            throw e;
        }catch(Exception e){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
    public void getTeamScheduleInfo(int userIdx, int teamScheduleIdx) throws BaseException{
        try{
            if(teamsDao.checkTeamScheduleIdxExist(teamScheduleIdx)!=1){
                throw new BaseException(BaseResponseStatus.SCHEDULE_DOES_NOT_EXIST); //존재하지 않는 스케줄
            }
        }catch(BaseException e){
            throw e;
        }catch(Exception e){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
    public void getUserInfo(int userIdx, int teamIdx) throws BaseException{
        try{
            if(teamsDao.checkTeamIdxExist(teamIdx)!=1){
                throw new BaseException(BaseResponseStatus.TEAM_DOES_NOT_EXIST); //존재하지 않는 팀 id
            }
        }catch(BaseException e){
            throw e;
        }catch(Exception e){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

}

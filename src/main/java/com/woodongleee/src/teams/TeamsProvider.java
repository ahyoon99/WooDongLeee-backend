package com.woodongleee.src.teams;

import com.woodongleee.config.BaseException;
import com.woodongleee.config.BaseResponseStatus;
import com.woodongleee.src.teams.model.GetTeamsinfoRes;
import com.woodongleee.src.teams.model.GetUserInfoRes;
import com.woodongleee.src.teams.model.GetTeamScheduleInfoRes;
import com.woodongleee.src.teams.model.GetTeamsScheduleRes;
import com.woodongleee.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamsProvider {
    private final TeamsDao teamsDao;
    private final JwtService jwtService;

    @Autowired
    public TeamsProvider(TeamsDao teamsDao, JwtService jwtService){
        this.teamsDao=teamsDao;
        this.jwtService=jwtService;
    }

    public List<GetTeamsinfoRes> getTeamsByTown(int userIdx, String town) throws BaseException{
        try{
            List<GetTeamsinfoRes> getTeamsinfoRes=teamsDao.getTeaminfoByTown(town);
            return getTeamsinfoRes;
        }catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetTeamsinfoRes getTeamsByName(int userIdx, String name) throws BaseException{
        try{
            GetTeamsinfoRes getTeamsinfoRes=teamsDao.getTeaminfoByName(name);
            return getTeamsinfoRes;
        }catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetTeamsinfoRes getTeaminfo(int userIdx, int teamIdx) throws BaseException{
        try{
            GetTeamsinfoRes getTeamsinfoRes=teamsDao.getTeaminfo(teamIdx);
            return getTeamsinfoRes;
        }catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }

    }

    public List<GetTeamsScheduleRes> getTeamsScheduleRes(int userIdx, int teamIdx, String startTime, String endTime) throws BaseException{
        try{
            List<GetTeamsScheduleRes> getTeamsScheduleRes=teamsDao.getTeamsScheduleRes(teamIdx, startTime, endTime);
            return getTeamsScheduleRes;
        }catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
    public GetTeamScheduleInfoRes getTeamScheduleInfoRes(int userIdx, int teamScheduleIdx) throws BaseException{
        try{
            GetTeamScheduleInfoRes getTeamScheduleInfoRes=teamsDao.getTeamScheduleInfoRes(teamScheduleIdx);
            return getTeamScheduleInfoRes;
        }catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public List<GetUserInfoRes> getUserInfoRes(int userIdx, int teamIdx) throws BaseException{
        try{
            List<GetUserInfoRes> getUserInfoRes=teamsDao.getUserInfoRes(teamIdx);
            return getUserInfoRes;
        }catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }


}

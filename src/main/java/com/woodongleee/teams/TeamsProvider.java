package com.woodongleee.teams;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.woodongleee.config.BaseException;
import com.woodongleee.config.BaseResponse;
import com.woodongleee.config.BaseResponseStatus;
import com.woodongleee.teams.model.GetTeamsinfoRes;
import com.woodongleee.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeamsProvider {
    private final TeamsDao teamsDao;
    private final JwtService jwtService;

    @Autowired
    public TeamsProvider(TeamsDao teamsDao, JwtService jwtService){
        this.teamsDao=teamsDao;
        this.jwtService=jwtService;
    }

    public GetTeamsinfoRes getTeamsByTown(int userIdx, String town) throws BaseException{
        try{
            GetTeamsinfoRes getTeamsinfoRes=teamsDao.getTeaminfoByTown(town);
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
       // if(teamsDao.getTeaminfo(teamIdx).getTeamIdx()==0){
       //     throw new BaseException(BaseResponseStatus.TEAM_DOES_NOT_EXIST);
       // }
        try{
            GetTeamsinfoRes getTeamsinfoRes=teamsDao.getTeaminfo(teamIdx);
            return getTeamsinfoRes;
        }catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }

    }


}

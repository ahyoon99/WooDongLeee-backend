package com.woodongleee.teams;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.woodongleee.config.BaseException;
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

    public GetTeamsinfoRes getTeamsByTown(String town) throws BaseException{
        try{
            GetTeamsinfoRes getTeamsinfoRes=teamsDao.getTeaminfoByTown(town);
            return getTeamsinfoRes;
        }catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetTeamsinfoRes getTeamsByName(String name) throws BaseException{
        try{
            GetTeamsinfoRes getTeamsinfoRes=teamsDao.getTeaminfoByName(name);
            return getTeamsinfoRes;
        }catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

}

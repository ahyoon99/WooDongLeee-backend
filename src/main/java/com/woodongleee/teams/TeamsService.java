package com.woodongleee.teams;

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

}

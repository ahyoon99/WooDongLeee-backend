package com.woodongleee.src.teams_2;

import com.woodongleee.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Teams_2Provider {
    private final Teams_2Dao teams2Dao;
    private final JwtService jwtService;

    @Autowired
    public Teams_2Provider(Teams_2Dao teams2Dao, JwtService jwtService){
        this.teams2Dao = teams2Dao;
        this.jwtService=jwtService;
    }



}

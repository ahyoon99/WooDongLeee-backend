package com.woodongleee.src.teamMatch;

import com.woodongleee.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeamMatchProvider {

    private final TeamMatchDao teamMatchDao;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public TeamMatchProvider(TeamMatchDao teamMatchDao, JwtService jwtService) {
        this.teamMatchDao = teamMatchDao;
        this.jwtService = jwtService;
    }
}

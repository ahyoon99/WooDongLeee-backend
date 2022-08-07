package com.woodongleee.src.teamMatch;

import com.woodongleee.config.BaseException;
import com.woodongleee.config.BaseResponseStatus;
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

    public int selectUserIdxByMatchPostIdx(int matchPostIdx) throws BaseException {
        // 팀 매칭 글이 존재하지 않습니다.
        if(teamMatchDao.existTeamMatchPostIdx(matchPostIdx) == 0){    // 팀 매칭글이 존재하지 않는 경우
            throw new BaseException(BaseResponseStatus.MATCH_POST_DOES_NOT_EXIST);
        }
        int userIdx = teamMatchDao.whoWritePost(matchPostIdx); // 매칭글 idx로 매칭글 작성한 유저 idx 찾기
        return userIdx;
    }
}

package com.woodongleee.src.teamMatch;

import com.woodongleee.config.BaseException;
import com.woodongleee.config.BaseResponse;
import com.woodongleee.config.BaseResponseStatus;
import com.woodongleee.src.teamMatch.model.PostTeamMatchPostsReq;
import com.woodongleee.src.teamMatch.model.PostTeamMatchPostsRes;
import com.woodongleee.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TeamMatchService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TeamMatchDao teamMatchDao;
    private final TeamMatchProvider teamMatchProvider;
    private final JwtService jwtService;


    @Autowired
    public TeamMatchService(TeamMatchDao teamMatchDao, TeamMatchProvider teamMatchProvider, JwtService jwtService) {
        this.teamMatchDao = teamMatchDao;
        this.teamMatchProvider = teamMatchProvider;
        this.jwtService = jwtService;
    }

    // 팀 매칭 글 작성
    public BaseResponse<PostTeamMatchPostsRes> createTeamMatchPost(int userIdx, PostTeamMatchPostsReq postTeamMatchPostsReq) throws BaseException{
        try{

            // 1. 팀 매칭글 생성은 리더만 가능합니다.
            if(teamMatchDao.isLeader(userIdx) == 0){    // 사용자가 리더가 아닌 경우
                return new BaseResponse<>(BaseResponseStatus.USER_NOT_LEADER);
            }

            // 2. 이미 팀 매칭 글 생성이 완료된 경기입니다.
            if(teamMatchDao.existTeamMatchPost(postTeamMatchPostsReq) >= 1){    // 이미 팀 매칭글이 존재하는 경우
                return new BaseResponse<>(BaseResponseStatus.ALREADY_EXIST_TEAM_MATCH_POST);
            }

            // 3. 존재하지 않는 팀 일정(경기)입니다.
            if(teamMatchDao.existTeamMatch(postTeamMatchPostsReq) == 0){    // 탐 일정이 존재하지 않는 경우
                return new BaseResponse<>(BaseResponseStatus.NO_EXIST_TEAM_MATCH);
            }

            // 4. 팀 매칭 글 작성 기한이 지났습니다. 경기 시작 2시간 전까지만 글 작성 가능.
            if(teamMatchDao.checkPostPeriod(postTeamMatchPostsReq) == 0) {    // 팀 매칭 글 작성 기간이 지난 경우
                return new BaseResponse<>(BaseResponseStatus.FINISH_POST_PERIOD);
            }

            int postIdx = teamMatchDao.insertMatchPost(userIdx, postTeamMatchPostsReq);
            return new BaseResponse<>(new PostTeamMatchPostsRes(postIdx, "TEAM"));

        }catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}

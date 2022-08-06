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

    public void teamApply(int userIdx, int teamIdx) throws BaseException {
        if (teamsDao.checkTeamIdxExist(teamIdx) != 1) {
            throw new BaseException(BaseResponseStatus.TEAM_DOES_NOT_EXIST);//존재하지 않는 팀 id
        }
        if (teamsDao.checkApply(teamIdx) != 1) {
            throw new BaseException(BaseResponseStatus.TEAM_NOT_RECRUITING); // 모집하지 않는 팀
        }
        if (teamsDao.checkTeamExist(userIdx)!=-1) {
            throw new BaseException(BaseResponseStatus.TEAM_ALREADY_EXIST); // 이미 가입된 팀 존재
        }
        try {
            teamsDao.teamApply(userIdx, teamIdx);
        }catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
    public void cancelTeamApply(int userIdx, int teamIdx) throws BaseException{
        if(teamsDao.checkTeamIdxExist(teamIdx)!=1){
            throw new BaseException(BaseResponseStatus.TEAM_DOES_NOT_EXIST); // 존재하지 않는 팀 id
        }
        if(teamsDao.checkStatus(userIdx, teamIdx)==1){
            throw new BaseException(BaseResponseStatus.CANCEL_NOT_AVAILABLE); // 취소를 할 수 없음
        }
        if(teamsDao.checkApplyExist(userIdx, teamIdx)!=1){
            throw new BaseException(BaseResponseStatus.TEAM_APPLY_DOES_NOT_EXIST); //신청 내역이 없습니다
        }
        try{
            teamsDao.cancelTeamApply(userIdx, teamIdx);
        }catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
    public void leaveTeam(int userIdx, int teamIdx) throws BaseException{
        if(teamsDao.checkTeamIdxExist(teamIdx)!=1){
            throw new BaseException(BaseResponseStatus.TEAM_DOES_NOT_EXIST); //존재하지 않는 팀 id
        }
        if(teamsDao.checkTeamExist(userIdx)!=teamIdx){
            throw new BaseException(BaseResponseStatus.NOT_TEAMMATE); //팀원이 아닙니다
        }
        try{
            teamsDao.leaveTeam(userIdx, teamIdx);
        }catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}

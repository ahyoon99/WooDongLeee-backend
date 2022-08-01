package com.woodongleee.src.teams;

import com.woodongleee.config.BaseException;
import com.woodongleee.config.BaseResponse;
import com.woodongleee.src.teams.model.GetTeamsinfoRes;
import com.woodongleee.src.teams.model.GetUserInfoRes;
import com.woodongleee.src.teams.model.GetTeamScheduleInfoRes;
import com.woodongleee.src.teams.model.GetTeamsScheduleRes;
import com.woodongleee.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teams")
public class TeamsController {

    @Autowired
    private final TeamsProvider teamsProvider;

    @Autowired
    private final TeamsService teamsService;

    @Autowired
    private final JwtService jwtService;

    public TeamsController(TeamsProvider teamsProvider, TeamsService teamsService, JwtService jwtService){
        this.teamsProvider=teamsProvider;
        this.teamsService=teamsService;
        this.jwtService=jwtService;
    }
    //동네로 팀 목록 조회
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetTeamsinfoRes>> getTeamsInfobyTown(@RequestParam String town){
        try{
            int userIdxByJwt= jwtService.getUserIdx();
            List<GetTeamsinfoRes> getTeamsinfoRes=teamsProvider.getTeamsByTown(userIdxByJwt, town);
            return new BaseResponse<>(getTeamsinfoRes);
        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //팀 이름으로 조회
    @ResponseBody
    @GetMapping("/name")
    public BaseResponse<GetTeamsinfoRes> getTeamsInfobyName(@RequestParam String name){
        try{
            int userIdxByJwt= jwtService.getUserIdx();
            GetTeamsinfoRes getTeamsinfoRes=teamsProvider.getTeamsByName(userIdxByJwt, name);
            return new BaseResponse<>(getTeamsinfoRes);
        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //팀 정보 조회
    @ResponseBody
    @GetMapping("/{teamIdx}")
    public BaseResponse<GetTeamsinfoRes> getTeaminfo(@PathVariable("teamIdx")int teamIdx){
        try{
            int userIdxByJwt= jwtService.getUserIdx();
            GetTeamsinfoRes getTeamsinfoRes=teamsProvider.getTeaminfo(userIdxByJwt, teamIdx);
            teamsService.getTeaminfo(userIdxByJwt, teamIdx);
            return new BaseResponse<>(getTeamsinfoRes);
        }catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //팀 일정 전부 조회
     @ResponseBody
     @GetMapping("/{teamIdx}/schedule")
     public BaseResponse<List<GetTeamsScheduleRes>> getTeamSchedule(@PathVariable("teamIdx")int teamIdx, @RequestParam String starDate, @RequestParam String endDate){
        try{
            int userIdxByJwt=jwtService.getUserIdx();
            List<GetTeamsScheduleRes> getTeamsScheduleRes=teamsProvider.getTeamsScheduleRes(userIdxByJwt, teamIdx, starDate, endDate);
            teamsService.getTeamSchedule(userIdxByJwt, teamIdx, starDate, endDate);
            return new BaseResponse<>(getTeamsScheduleRes);
        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    //특정 일정 정보 조회
    @ResponseBody
    @GetMapping("/schedule/{teamScheduleIdx}")
    public BaseResponse<GetTeamScheduleInfoRes> getTeamScheduleInfo(@PathVariable("teamScheduleIdx")int teamScheduleIdx){
        try{
            int userIdxByJwt= jwtService.getUserIdx();
            GetTeamScheduleInfoRes getTeamScheduleInfoRes=teamsProvider.getTeamScheduleInfoRes(userIdxByJwt, teamScheduleIdx);
            return new BaseResponse<>(getTeamScheduleInfoRes);
        }catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    //팀 일정 참여 투표//

    //팀원 목록 조회//
    @ResponseBody
    @GetMapping("/{teamIdx}/members")
    public BaseResponse<List<GetUserInfoRes>> getUserInfo(@PathVariable("teamIdx")int teamIdx){
        try{
            int userIdByJwt= jwtService.getUserIdx();
            List<GetUserInfoRes> getUserInfoRes=teamsProvider.getUserInfoRes(userIdByJwt, teamIdx);
            teamsService.getUserInfo(userIdByJwt, teamIdx);
            return new BaseResponse<>(getUserInfoRes);
        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}

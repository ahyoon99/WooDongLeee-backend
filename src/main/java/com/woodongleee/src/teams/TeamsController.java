package com.woodongleee.src.teams;

import com.woodongleee.config.BaseException;
import com.woodongleee.config.BaseResponse;
import com.woodongleee.config.BaseResponseStatus;
import com.woodongleee.src.teams.model.*;
import com.woodongleee.src.user.UserProvider;
import com.woodongleee.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import static com.woodongleee.utils.ValidationRegex.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/teams")
public class TeamsController {

    @Autowired
    private final TeamsProvider teamsProvider;

    @Autowired
    private final TeamsService teamsService;

    @Autowired
    private final JwtService jwtService;

    @Autowired
    private final UserProvider userProvider;

    public TeamsController(TeamsProvider teamsProvider, TeamsService teamsService, JwtService jwtService, UserProvider userProvider){
        this.teamsProvider=teamsProvider;
        this.teamsService=teamsService;
        this.jwtService=jwtService;
        this.userProvider=userProvider;
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
    @ResponseBody
    @PostMapping("/{teamIdx}/schedule/{teamScheduleIdx}")
    public BaseResponse<String> vote(@PathVariable("teamIdx")int teamIdx, @PathVariable("teamScheduleIdx")int teamScheduleIdx){
        try{
            int userIdByJwt= jwtService.getUserIdx();
            teamsService.vote(userIdByJwt, teamIdx, teamScheduleIdx);
            String result="투표 완료하였습니다.";
            return new BaseResponse<>(result);
        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PatchMapping("/{teamIdx}/schedule/{teamScheduleIdx}")
    public BaseResponse<String> cancelVote(@PathVariable("teamIdx")int teamIdx, @PathVariable("teamScheduleIdx")int teamScheduleIdx){
        try{
            int userIdxByJwt= jwtService.getUserIdx();
            teamsService.cancelVote(userIdxByJwt, teamIdx, teamScheduleIdx);
            String result="일정 투표를 취소하였습니다.";
            return new BaseResponse<>(result);
        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    //팀원 목록 조회//
    @ResponseBody
    @GetMapping("/{teamIdx}/members")
    public BaseResponse<List<GetUserInfoRes>> getUserInfo(@PathVariable("teamIdx")int teamIdx){
        try{
            int userIdByJwt= jwtService.getUserIdx();
            List<GetUserInfoRes> getUserInfoRes=teamsProvider.getUserInfoRes(userIdByJwt, teamIdx);
            return new BaseResponse<>(getUserInfoRes);
        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //팀 가입 신청
    @ResponseBody
    @PostMapping("/{teamIdx}/apply")
    public BaseResponse<String> teamApply(@PathVariable("teamIdx")int teamIdx){
        try{
            int userIdxByJwt= jwtService.getUserIdx();
            teamsService.teamApply(userIdxByJwt, teamIdx);
            String result="팀 신청이 완료되었습니다";
            return new BaseResponse<>(result);
        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //팀 가입 신청 취소
    @ResponseBody
    @PatchMapping("/{teamIdx}/apply")
    public BaseResponse<String> cancelTeamApply(@PathVariable("teamIdx") int teamIdx){
        try{
            int userIdxByJwt= jwtService.getUserIdx();
            teamsService.cancelTeamApply(userIdxByJwt, teamIdx);
            String result="팀 신청이 취소되었습니다";
            return new BaseResponse<>(result);
        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //팀 탈퇴
    @ResponseBody
    @PatchMapping("/{teamIdx}/users/leave")
    public BaseResponse<String> leaveTeam(@PathVariable("teamIdx") int teamIdx){
        try{
            int userIdxByJwt= jwtService.getUserIdx();
            teamsService.leaveTeam(userIdxByJwt, teamIdx);
            String result="팀을 탈퇴하였습니다.";
            return new BaseResponse<>(result);
        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //팀 순위 목록 조회(지역별로 필터링)
    @ResponseBody
    @GetMapping("/ranking")
    public BaseResponse<List<GetTeamsinfoRes>> getTeamsbyRank(@RequestParam String town){
        try{
            int userIdxByJwt=jwtService.getUserIdx();
            List<GetTeamsinfoRes> getTeamsbyRankRes=teamsProvider.getTeamsbyRank(userIdxByJwt, town);
            return new BaseResponse<>(getTeamsbyRankRes);
        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //팀 생성
    @ResponseBody
    @PostMapping
    public BaseResponse<String> createTeam(@RequestBody PostTeamReq newTeam){
        Object[] Params= new Object[]{newTeam.getName(), newTeam.getTown(), newTeam.getTeamProfileImgUrl(), newTeam.getIntroduce()};
        if(Arrays.stream(Params).anyMatch(Objects::isNull)){
            return new BaseResponse<>(BaseResponseStatus.EMPTY_PARAMETER); //빈칸을 채워주세요
        }
        try{
            int userIdxByJwt= jwtService.getUserIdx();
            teamsService.createTeam(userIdxByJwt, newTeam);
            //teamsService.updateTeam(userIdxByJwt, newTeam);
            String result="팀을 새로 생성하였습니다.";
            return new BaseResponse<>(result);
        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }
    //팀 이름 중복 검사
    @ResponseBody
    @GetMapping("/is-duplicated")
    public BaseResponse<String> isNameDuplicated(@RequestParam String name){
        try{
            if(!isRegexTeamName(name)){
                return new BaseResponse<>(BaseResponseStatus.INVALID_TEAM_NAME_PATTERN); //잘못된 팀 형식
            }
            int userIdxByJwt= jwtService.getUserIdx();
            if(teamsProvider.isNameDuplicated(userIdxByJwt, name)) {
                return new BaseResponse<>(BaseResponseStatus.DUPLICATED_TEAM_NAME); //중복된 팀 이름//
            }
        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
        String result="중복 검사 성공";
        return new BaseResponse<>(result);
    }

    //팀 정보 수정(리더만 가능)
    @ResponseBody
    @PatchMapping("/{teamIdx}")
    public BaseResponse<String> modifyTeamInfo(@PathVariable int teamIdx, @RequestBody ModifyTeamInfoReq modifyTeam){
        try{
            int userIdxByJwt= jwtService.getUserIdx();
            teamsService.modifyTeamInfo(userIdxByJwt, teamIdx, modifyTeam);
            String result="정보 수정이 완료되었습니다.";
            return new BaseResponse<>(result);
        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}

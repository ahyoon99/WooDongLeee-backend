package com.woodongleee.src.teams_2;

import com.woodongleee.config.BaseException;
import com.woodongleee.config.BaseResponse;
import com.woodongleee.src.teams_2.model.AddTeamScheduleReq;
import com.woodongleee.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teams")
public class Teams_2Controller {

    private final Teams_2Provider teams2Provider;

    private final Teams_2Service teams2Service;

    private final JwtService jwtService;

    @Autowired
    public Teams_2Controller(Teams_2Provider teams2Provider, Teams_2Service teams2Service, JwtService jwtService){
        this.teams2Provider = teams2Provider;
        this.teams2Service = teams2Service;
        this.jwtService=jwtService;
    }

    // 팀 모집 활성화/비활성화
    @PatchMapping("{teamIdx}/recruit")
    public BaseResponse<String> changeTeamRecruit(@PathVariable int teamIdx){
        try{
            int userIdx = jwtService.getUserIdx();
            int result = teams2Service.changeTeamRecruit(userIdx, teamIdx);

            if(result == 1){
                return new BaseResponse<>("팀원 모집 활성화 완료");
            }
            else return new BaseResponse<>("팀원 모집 비활성화 완료");

        } catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    // 팀 일정 추가
    @PostMapping("{teamIdx}/schedule")
    public BaseResponse<String> addTeamSchedule(@RequestBody AddTeamScheduleReq addTeamScheduleReq, @PathVariable int teamIdx){
        try{
            int userIdx = jwtService.getUserIdx();
            teams2Service.addTeamSchedule(addTeamScheduleReq, teamIdx, userIdx);

            String result = "팀원 일정 추가 완료";
            return new BaseResponse<>(result);
        } catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    // 팀 해체
    @PatchMapping("{teamIdx}/status")
    public BaseResponse<String> disbandTeam(@PathVariable int teamIdx){
        try {
            int userIdx = jwtService.getUserIdx();
            teams2Service.disbandTeam(userIdx, teamIdx);
            String result = "팀 해체 성공";
            return new BaseResponse<>(result);
        } catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }
}

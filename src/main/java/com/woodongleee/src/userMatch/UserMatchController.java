package com.woodongleee.src.userMatch;

import com.woodongleee.config.BaseException;
import com.woodongleee.config.BaseResponse;
import com.woodongleee.config.BaseResponseStatus;
import com.woodongleee.src.userMatch.Domain.*;
import com.woodongleee.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("user-match")
public class UserMatchController {

    private final UserMatchProvider userMatchProvider;
    private final UserMatchService userMatchService;
    private final JwtService jwtService;

    @Autowired
    public UserMatchController(UserMatchProvider userMatchProvider, UserMatchService userMatchService, JwtService jwtService){
        this.userMatchProvider = userMatchProvider;
        this.userMatchService = userMatchService;
        this.jwtService = jwtService;
    }


    // 용병 모집글 조회 API
    @GetMapping("")
    public BaseResponse<List<GetUserMatchPostInfoRes>> getUserMatchPosts(@RequestParam String town,
                                                                         @RequestParam String startTime,
                                                                         @RequestParam String endTime){
        try{
            int userIdx = jwtService.getUserIdx();
            List<GetUserMatchPostInfoRes> MatchPosts = userMatchProvider.getUserMatchPosts(userIdx, town, startTime, endTime);
            return new BaseResponse<>(MatchPosts);
        }
        catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }



    // 용병 신청 API
    @PostMapping("/{matchPostIdx}/apply")
    public BaseResponse<String> applyUserMatch(@PathVariable("matchPostIdx") int matchPostIdx){
        try{
            int userIdx = jwtService.getUserIdx();
            userMatchService.applyUserMatch(userIdx, matchPostIdx);

            String result = "용병 신청을 성공하였습니다.";
            return new BaseResponse<>(result);
        }
        catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    // 용병 신청 취소 API
    @PatchMapping("/{matchPostIdx}/apply")
    public BaseResponse<String> cancelApplyUserMatch(@PathVariable("matchPostIdx") int matchPostIdx){
        try{
            int userIdx = jwtService.getUserIdx();
            userMatchService.cancelApplyUserMatch(userIdx, matchPostIdx);

            String result = "용병 신청을 취소하였습니다.";
            return new BaseResponse<>(result);
        }
        catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }


    //용병 모집글 생성
    @PostMapping("{teamScheduleIdx}")
    public BaseResponse<CreateUserMatchPostRes> createUserMatchPost(@RequestBody CreateUserMatchPostReq createUserMatchPostReq,
                                                                    @PathVariable int teamScheduleIdx){
        try{
            int userIdx = jwtService.getUserIdx();
            if(userIdx != createUserMatchPostReq.getUserIdx()){
                return new BaseResponse<>(BaseResponseStatus.INVALID_JWT);
            }

            return userMatchService.createUserMatchPost(userIdx, teamScheduleIdx, createUserMatchPostReq.getContents());


        }
        catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }





}

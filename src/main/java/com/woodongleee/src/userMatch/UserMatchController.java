package com.woodongleee.src.userMatch;

import com.woodongleee.config.BaseException;
import com.woodongleee.config.BaseResponse;
import com.woodongleee.config.BaseResponseStatus;
import com.woodongleee.src.user.UserProvider;
import com.woodongleee.src.userMatch.model.*;
import com.woodongleee.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.woodongleee.config.BaseResponseStatus.*;

@RestController
@RequestMapping("user-match")
public class UserMatchController {

    private final UserMatchProvider userMatchProvider;
    private final UserMatchService userMatchService;
    private final JwtService jwtService;

    private final UserProvider userProvider;

    @Autowired
    public UserMatchController(UserMatchProvider userMatchProvider, UserMatchService userMatchService, JwtService jwtService, UserProvider userProvider){
        this.userMatchProvider = userMatchProvider;
        this.userMatchService = userMatchService;
        this.jwtService = jwtService;
        this.userProvider = userProvider;
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
        Object[] params = new Object[]{
                createUserMatchPostReq.getUserIdx(),
                createUserMatchPostReq.getContents()
        };
        if(Arrays.stream(params).anyMatch(Objects::isNull)){
            return new BaseResponse<>(EMPTY_PARAMETER);
        }

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

    //용병 모집글 수정
    @PatchMapping("{teamScheduleIdx}")
    public BaseResponse<ModifyUserMatchPostRes> modifyUserMatchPost(@RequestBody ModifyUserMatchPostReq modifyUserMatchPostReq,
                                                                    @PathVariable int teamScheduleIdx){
        Object[] params = new Object[]{
                modifyUserMatchPostReq.getUserIdx(),
                modifyUserMatchPostReq.getContents()
        };
        if(Arrays.stream(params).anyMatch(Objects::isNull)){
            return new BaseResponse<>(EMPTY_PARAMETER);
        }

        try{
            int userIdx = jwtService.getUserIdx();
            if(userIdx != modifyUserMatchPostReq.getUserIdx()){
                return new BaseResponse<>(BaseResponseStatus.INVALID_JWT);
            }

            return userMatchService.modifyUserMatchPost(userIdx, teamScheduleIdx, modifyUserMatchPostReq.getContents());


        }
        catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    // 용병 모집글 삭제
    @DeleteMapping("{teamScheduleIdx}")
    public BaseResponse<String> deleteUserMatchPost(@PathVariable int teamScheduleIdx){
        try{
            int userIdx = jwtService.getUserIdx();

            userMatchService.deleteUserMatchPost(userIdx, teamScheduleIdx);
            String result = "용병 모집글 삭제를 완료하였습니다.";
            return new BaseResponse<>(result);
        }
        catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    // 용병 신청 목록 조회
    @GetMapping("{teamScheduleIdx}/apply")
    public BaseResponse<List<UserMatchApplyInfo>> getUserMatchApplyList(@PathVariable int teamScheduleIdx){
        try{
            int userIdx = jwtService.getUserIdx();

            List<UserMatchApplyInfo> userMatchApplyInfoList = userMatchProvider.getUserMatchApplyList(userIdx, teamScheduleIdx);
            return new BaseResponse<>(userMatchApplyInfoList);
        }
        catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    // 용병 신청 승인
    @PatchMapping("/apply/{matchApplyIdx}/accept")
    public BaseResponse<String> acceptUserMatchApply(@PathVariable int matchApplyIdx){
        try{
            int userIdx = jwtService.getUserIdx();

            userMatchService.acceptUserMatchApply(userIdx, matchApplyIdx);
            String result = "용병 매칭 신청을 승인 하였습니다.";
            return new BaseResponse<>(result);
        }
        catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @PatchMapping("/apply/{matchApplyIdx}/reject")
    public BaseResponse<String> rejectUserMatchApply(@PathVariable int matchApplyIdx){
        try{
            int userIdx = jwtService.getUserIdx();

            userMatchService.rejectUserMatchApply(userIdx, matchApplyIdx);
            String result = "용병 매칭 신청을 거절 하였습니다.";
            return new BaseResponse<>(result);
        }
        catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }


}

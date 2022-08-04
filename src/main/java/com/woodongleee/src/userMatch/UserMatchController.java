package com.woodongleee.src.userMatch;

import com.woodongleee.config.BaseException;
import com.woodongleee.config.BaseResponse;
import com.woodongleee.config.BaseResponseStatus;
import com.woodongleee.src.user.UserProvider;
import com.woodongleee.src.user.model.GetUserByJwtRes;
import com.woodongleee.src.userMatch.Domain.*;
import com.woodongleee.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.woodongleee.config.BaseResponseStatus.EMPTY_PARAMETER;

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
            //GetUserByJwtRes getUserByJwtRes = userProvider.getUserByJwt(userIdx);

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
            //GetUserByJwtRes getUserByJwtRes = userProvider.getUserByJwt(userIdx);

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
            //GetUserByJwtRes getUserByJwtRes = userProvider.getUserByJwt(userIdx);

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
            //GetUserByJwtRes getUserByJwtRes = userProvider.getUserByJwt(userIdx);

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
            //GetUserByJwtRes getUserByJwtRes = userProvider.getUserByJwt(userIdx);

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
            userProvider.checkUserStatus(userIdx);
            userMatchService.deleteUserMatchPost(userIdx, teamScheduleIdx);
            String result = "용병 모집글 삭제를 완료하였습니다.";
            return new BaseResponse<>(result);
        }
        catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }



}

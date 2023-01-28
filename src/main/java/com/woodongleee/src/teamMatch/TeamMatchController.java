package com.woodongleee.src.teamMatch;

import com.woodongleee.config.BaseResponse;
import com.woodongleee.config.BaseException;
import com.woodongleee.config.BaseResponseStatus;
import com.woodongleee.src.teamMatch.model.*;
import com.woodongleee.utils.JwtService;
import com.woodongleee.utils.SHA256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/team-match")
public class TeamMatchController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TeamMatchProvider teamMatchProvider;
    private final TeamMatchService teamMatchService;
    private final JwtService jwtService;
    public static final int POST_LENGTH_MAX = 500;
    public static final int VALID_SCORE_MIN = 0;

    public TeamMatchController(TeamMatchProvider teamMatchProvider, TeamMatchService teamMatchService, JwtService jwtService) {
        this.teamMatchProvider = teamMatchProvider;
        this.teamMatchService = teamMatchService;
        this.jwtService = jwtService;
    }

    @ResponseBody
    @PostMapping("/{teamScheduleIdx}")
    public BaseResponse<PostTeamMatchPostsRes> createTeamMatchPosts(@PathVariable("teamScheduleIdx") int teamScheduleIdx, @RequestBody PostTeamMatchPostsReq postTeamMatchPostsReq) throws Exception{
        try {
            int userIdxByJwt = jwtService.getUserIdx();

            if(!postTeamMatchPostsReq.checkUserIdxAndUserIdxByJWT(userIdxByJwt)){
                return new BaseResponse<>(BaseResponseStatus.INVALID_JWT);
            }
            if (postTeamMatchPostsReq.checkContentsMaxLength(POST_LENGTH_MAX)) {     // 게시글의 길이에 대한 validation
                return new BaseResponse<>(BaseResponseStatus.POST_POSTS_INVALID_CONTENTS);
            }

            BaseResponse<PostTeamMatchPostsRes> postTeamMatchPostsRes = teamMatchService.createTeamMatchPost(postTeamMatchPostsReq.getUserIdx(), postTeamMatchPostsReq);
            return postTeamMatchPostsRes;
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping("/{matchPostIdx}")
    public BaseResponse<ModifyTeamMatchPostsRes> modifyTeamMatchPosts(@PathVariable int matchPostIdx, @RequestBody ModifyTeamMatchPostsReq modifyTeamMatchPostsReq){
        try {
            int userIdxByJwt = jwtService.getUserIdx();

            if(!modifyTeamMatchPostsReq.checkUserIdxAndUserIdxByJWT(userIdxByJwt)){
                return new BaseResponse<>(BaseResponseStatus.INVALID_JWT);
            }
            if(modifyTeamMatchPostsReq.checkContentsMaxLength(POST_LENGTH_MAX)){     // 게시글의 길이에 대한 validation
                return new BaseResponse<>(BaseResponseStatus.POST_POSTS_INVALID_CONTENTS);
            }

            BaseResponse<ModifyTeamMatchPostsRes> modifyTeamMatchPostsRes = teamMatchService.modifyTeamMatchPost(modifyTeamMatchPostsReq, matchPostIdx);
            return modifyTeamMatchPostsRes;
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @DeleteMapping("/{matchPostIdx}")
    public BaseResponse<String> deleteTeamMatchPosts(@PathVariable int matchPostIdx){
        try {
            int userIdx = teamMatchProvider.selectUserIdxByMatchPostIdx(matchPostIdx);  // 매칭글 idx로 매칭글 작성한 유저 idx 찾기
            int userIdxByJwt = jwtService.getUserIdx();

            if(!teamMatchProvider.checkUserIdxAndUserIdxByJWT(userIdx, userIdxByJwt)){
                return new BaseResponse<>(BaseResponseStatus.INVALID_JWT);
            }

            teamMatchService.deleteTeamMatchPosts(userIdxByJwt, matchPostIdx);

            String result = "삭제를 성공했습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("/{matchPostIdx}/apply")
    public BaseResponse<String> applyTeamMatch(@PathVariable int matchPostIdx){
        try {
            int userIdxByJwt = jwtService.getUserIdx();

            teamMatchService.applyTeamMatch(userIdxByJwt, matchPostIdx);

            String result = "팀 매칭 신청을 성공하였습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping("/{matchPostIdx}/apply")
    public BaseResponse<String> cancelApplyTeamMatch(@PathVariable int matchPostIdx){
        try {
            int userIdxByJwt = jwtService.getUserIdx();

            teamMatchService.cancelApplyTeamMatch(userIdxByJwt, matchPostIdx);

            String result = "팀 매칭 신청 취소를 성공하였습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
    
    @ResponseBody
    @PostMapping("/result/{teamScheduleIdx}")
    public BaseResponse<PostGameResultRes> postGameResult(@PathVariable("teamScheduleIdx") int teamScheduleIdx, @RequestBody PostGameResultReq postGameResultReq){
        try {
            // teamScheduleIdx에서 homeIdx(팀 idx), awayIdx(팀 idx) 가져오기
            int homeTeamIdx = teamMatchProvider.selectHomeIdxByTeamScheduleIdx(teamScheduleIdx);
            // homeIdx를 가지는 리더 userIdx 가져오기, 결과 입력은 home팀의 주장만 가능!
            int leaderUserIdx = teamMatchProvider.selectLeaderIdxByTeamIdx(homeTeamIdx);
            int userIdxByJwt = jwtService.getUserIdx();

            if(!teamMatchProvider.checkUserIdxAndUserIdxByJWT(leaderUserIdx, userIdxByJwt)){
                return new BaseResponse<>(BaseResponseStatus.INVALID_JWT);
            }
            if(!postGameResultReq.checkAwayScoreMinValue(VALID_SCORE_MIN)){  // 원정팀 점수에 대한 validation
                return new BaseResponse<>(BaseResponseStatus.INVALID_SCORE_SCOPE);
            }
            if (!postGameResultReq.checkHomeScoreMinValue(VALID_SCORE_MIN)) {     // 홈팀 점수에 대한 validation
                return new BaseResponse<>(BaseResponseStatus.INVALID_SCORE_SCOPE);
            }

            BaseResponse<PostGameResultRes> postGameResultRes = teamMatchService.postGameResult(postGameResultReq);
            return postGameResultRes;
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/{teamScheduleIdx}/apply")
    public BaseResponse<List<GetApplyTeamRes>> getApplyTeam(@PathVariable int teamScheduleIdx) {
        try {
            // teamScheduleIdx를 이용해서 작성자의 userIdx 찾아오기
            int userIdx = teamMatchProvider.selectUserIdxByTeamScheduleIdx(teamScheduleIdx);
            int userIdxByJwt = jwtService.getUserIdx();

            if(!teamMatchProvider.checkUserIdxAndUserIdxByJWT(userIdx, userIdxByJwt)){
                return new BaseResponse<>(BaseResponseStatus.INVALID_JWT);
            }

            List<GetApplyTeamRes> getApplyTeamResList = teamMatchProvider.getApplyTeam(teamScheduleIdx);
            return new BaseResponse<>(getApplyTeamResList);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
    
    @ResponseBody
    @PatchMapping("/apply/{matchApplyIdx}/reject")
    public BaseResponse<String> rejectTeamMatchApply(@PathVariable int matchApplyIdx){
        try {
            // matchApplyIdx를 이용하여 home팀의 leaderIdx 가져오기
            int leaderUserIdx = teamMatchProvider.selectHomeLeaderIdxByMatchApplyIdx(matchApplyIdx);
            int userIdxByJwt = jwtService.getUserIdx();

            if(!teamMatchProvider.checkUserIdxAndUserIdxByJWT(leaderUserIdx, userIdxByJwt)){
                return new BaseResponse<>(BaseResponseStatus.INVALID_JWT);
            }

            teamMatchService.rejectTeamMatchApply(userIdxByJwt, matchApplyIdx);

            String result = "팀 매칭 신청 거절을 성공하였습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
    
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetTeamMatchPostRes>> getTeamMatchPosts(@RequestParam String town, @RequestParam String startTime, @RequestParam String endTime) {
        try {
            int userIdxByJwt = jwtService.getUserIdx();

            List<GetTeamMatchPostRes> getTeamMatchPostResList = teamMatchProvider.getTeamMatchPosts(userIdxByJwt, town, startTime, endTime);
            return new BaseResponse<>(getTeamMatchPostResList);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
    
    @ResponseBody
    @PatchMapping("/apply/{matchApplyIdx}/accept")
    public BaseResponse<String> acceptTeamMatchApply(@PathVariable int matchApplyIdx){
        try {
            // matchApplyIdx를 이용하여 home팀의 leaderIdx 가져오기
            int leaderUserIdx = teamMatchProvider.selectHomeLeaderIdxByMatchApplyIdx(matchApplyIdx);
            int userIdxByJwt = jwtService.getUserIdx();

            if(!teamMatchProvider.checkUserIdxAndUserIdxByJWT(leaderUserIdx, userIdxByJwt)){
                return new BaseResponse<>(BaseResponseStatus.INVALID_JWT);
            }

            teamMatchService.acceptTeamMatchApply(userIdxByJwt, matchApplyIdx);

            String result = "팀 매칭 신청 승인을 성공하였습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}


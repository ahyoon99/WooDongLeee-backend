package com.woodongleee.src.teamMatch;

import com.woodongleee.config.BaseResponse;
import com.woodongleee.config.BaseException;
import com.woodongleee.config.BaseResponseStatus;
import com.woodongleee.src.teamMatch.model.ModifyTeamMatchPostsReq;
import com.woodongleee.src.teamMatch.model.ModifyTeamMatchPostsRes;
import com.woodongleee.src.teamMatch.model.PostTeamMatchPostsReq;
import com.woodongleee.src.teamMatch.model.PostTeamMatchPostsRes;
import com.woodongleee.utils.JwtService;
import com.woodongleee.utils.SHA256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/team-match")
public class TeamMatchController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TeamMatchProvider teamMatchProvider;
    private final TeamMatchService teamMatchService;
    private final JwtService jwtService;

    public TeamMatchController(TeamMatchProvider teamMatchProvider, TeamMatchService teamMatchService, JwtService jwtService) {
        this.teamMatchProvider = teamMatchProvider;
        this.teamMatchService = teamMatchService;
        this.jwtService = jwtService;
    }

    @ResponseBody
    @PostMapping("/{teamScheduleIdx}")
    public BaseResponse<PostTeamMatchPostsRes> createTeamMatchPosts(@PathVariable("teamScheduleIdx") int teamScheduleIdx, @RequestBody PostTeamMatchPostsReq postTeamMatchPostsReq){
        try {

            int userIdxByJwt = jwtService.getUserIdx();
            if(postTeamMatchPostsReq.getUserIdx() != userIdxByJwt){
                return new BaseResponse<>(BaseResponseStatus.INVALID_JWT);
            }

            if (postTeamMatchPostsReq.getContents().length() > 500) {     // 게시글의 길이에 대한 validation
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
            if(modifyTeamMatchPostsReq.getUserIdx() != userIdxByJwt){
                return new BaseResponse<>(BaseResponseStatus.INVALID_JWT);
            }

            if(modifyTeamMatchPostsReq.getContents().length()>500){     // 게시글의 길이에 대한 validation
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
            if(userIdx != userIdxByJwt){
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
    @PatchMapping("/apply/{matchApplyIdx}/accept")
    public BaseResponse<String> acceptTeamMatchApply(@PathVariable int matchApplyIdx){
        try {
            // matchApplyIdx를 이용하여 home팀의 leaderIdx 가져오기
            int userIdx = teamMatchProvider.selectHomeLeaderIdxByMatchApplyIdx(matchApplyIdx);

            int userIdxByJwt = jwtService.getUserIdx();
            if(userIdx != userIdxByJwt){
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


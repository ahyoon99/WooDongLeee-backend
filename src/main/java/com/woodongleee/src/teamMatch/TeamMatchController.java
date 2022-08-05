package com.woodongleee.src.teamMatch;

import com.woodongleee.config.BaseResponse;
import com.woodongleee.config.BaseException;
import com.woodongleee.config.BaseResponseStatus;
import com.woodongleee.src.teamMatch.model.PostTeamMatchPostsReq;
import com.woodongleee.src.teamMatch.model.PostTeamMatchPostsRes;
import com.woodongleee.utils.JwtService;
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

            // jwt 관련 부분은 merge 후, 테스트 해보겠습니다.
//            int userIdxByJwt = jwtService.getUserIdx();
//            if(postTeamMatchPostsReq.getUserIdx() != userIdxByJwt){
//                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
//            }

            if (postTeamMatchPostsReq.getContents().length() > 500) {     // 게시글의 길이에 대한 validation
                return new BaseResponse<>(BaseResponseStatus.POST_POSTS_INVALID_CONTENTS);
            }

            BaseResponse<PostTeamMatchPostsRes> postTeamMatchPostsRes = teamMatchService.createTeamMatchPost(postTeamMatchPostsReq.getUserIdx(), postTeamMatchPostsReq);
            return postTeamMatchPostsRes;

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}

package com.woodongleee.src.teamMatch;

import com.woodongleee.src.teamMatch.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.text.ParseException;
import java.util.List;

@Repository
public class TeamMatchDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int insertMatchPost(int userIdx, PostTeamMatchPostsReq postTeamMatchPostsReq) {
        String insertMatchPostQuery = "INSERT INTO MatchPost(userIdx, teamScheduleIdx, contents, type) VALUES (?,?,?,?)";
        Object []insertMatchPostParams = new Object[] {userIdx, postTeamMatchPostsReq.getTeamScheduleIdx(),postTeamMatchPostsReq.getContents(),"TEAM"};
        this.jdbcTemplate.update(insertMatchPostQuery, insertMatchPostParams);

        String lastInsertIdxQuery = "select last_insert_id()";

        return jdbcTemplate.queryForObject(lastInsertIdxQuery, int.class);
    }

    public int existTeamMatchPost(int teamScheduleIdx) {
        String existTeamMatchPostQuery = "select count(*) from MatchPost where teamScheduleIdx = ?";
        int existTeamMatchPostParams = teamScheduleIdx;
        return this.jdbcTemplate.queryForObject(existTeamMatchPostQuery, int.class, existTeamMatchPostParams);
    }

    public String checkUserStatus(int userIdx) {
        String checkUserStatusQuery = "select status from User where userIdx=?";
        int checkUserStatusParams = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserStatusQuery, String.class, checkUserStatusParams);
    }

    public String isLeader(int userIdx) {  // 사용자가 리더가 아닌 경우, 0 리턴
        String isLeaderQuery = "select isLeader from User where userIdx=?";
        int isLeaderParams = userIdx;
        return this.jdbcTemplate.queryForObject(isLeaderQuery, String.class, isLeaderParams);
    }

    public int existTeamMatch(int teamScheduleIdx) {    // 탐 일정이 존재하지 않는 경우, 0 리턴
        String existTeamMatchQuery = "select count(*) from teamSchedule where teamScheduleIdx=?";
        int existTeamMatchParams = teamScheduleIdx;
        return this.jdbcTemplate.queryForObject(existTeamMatchQuery, int.class, existTeamMatchParams);
    }

    public String selectStartTime(int teamScheduleIdx) throws ParseException {   // 팀 매칭 글 작성 기간이 지난 경우, 0 리턴
        String selectStartTimeQuery = "select startTime from teamSchedule where teamScheduleIdx=?";
        int selectStartTimeParams = teamScheduleIdx;
        return this.jdbcTemplate.queryForObject(selectStartTimeQuery, String.class, selectStartTimeParams);
    }

    public int findUserIdxByPostIdx(int matchPostIdx) {
        String findUserIdxByPostIdxQuery = "select userIdx from MatchPost where matchPostIdx=?";
        int findUserIdxByPostIdxParams = matchPostIdx;
        return this.jdbcTemplate.queryForObject(findUserIdxByPostIdxQuery, int.class, findUserIdxByPostIdxParams);
    }

    public int existTeamMatchPostIdx(int matchPostIdx) {
        String existTeamMatchPostIdxQuery = "select count(*) from MatchPost where matchPostIdx = ?";
        int existTeamMatchPostIdxParams = matchPostIdx;
        return this.jdbcTemplate.queryForObject(existTeamMatchPostIdxQuery, int.class, existTeamMatchPostIdxParams);
    }

    public int updateMatchPost(int matchPostIdx, String contents) {
        String updateMatchPostQuery = "update MatchPost set contents=? where matchpostIdx=?";
        Object [] updateMatchPostParams = new Object[] {contents, matchPostIdx};
        return this.jdbcTemplate.update(updateMatchPostQuery, updateMatchPostParams);
    }

    public int deleteTeamMatchPosts(int matchPostIdx) {
        String deleteTeamMatchPostsQuery = "delete from MatchPost where matchPostIdx=?";
        Object [] deleteTeamMatchPostsParams = new Object[] {matchPostIdx};
        return this.jdbcTemplate.update(deleteTeamMatchPostsQuery, deleteTeamMatchPostsParams);
    }

    public int selectScheduleIdxByMatchPostIdx(int matchPostIdx) {
        String findTeamScheduleIdxByMatchPostIdxQuery = "select teamScheduleIdx from MatchPost where matchPostIdx = ?";
        int findTeamScheduleIdxByMatchPostIdxParams = matchPostIdx;
        return this.jdbcTemplate.queryForObject(findTeamScheduleIdxByMatchPostIdxQuery, int.class, findTeamScheduleIdxByMatchPostIdxParams);
    }

    public int checkApplyStatus(int matchPostIdx) {
        String checkApplyStatusQuery = "select count(*) from MatchApply where status='ACCEPTED' and matchPostIdx = ?";
        int checkApplyStatusParams = matchPostIdx;
        return this.jdbcTemplate.queryForObject(checkApplyStatusQuery, int.class, checkApplyStatusParams);
    }

    public void applyTeamMatch(int userIdx, int matchPostIdx) {
        String applyTeamMatchQuery = "INSERT INTO MatchApply(userIdx, matchPostIdx, status) VALUES (?,?,?);";
        Object []applyTeamMatchParams = new Object[] {userIdx, matchPostIdx,"APPLIED"};
        this.jdbcTemplate.update(applyTeamMatchQuery, applyTeamMatchParams);
    }

    public String checkAlreadyApplyStatus(int userIdxByJwt, int matchPostIdx) {
        String checkAlreadyApplyStatusQuery = "select status from MatchApply where userIdx=? and matchPostIdx=?";
        Object []checkAlreadyApplyStatusParams = new Object[] {userIdxByJwt, matchPostIdx};
        return this.jdbcTemplate.queryForObject(checkAlreadyApplyStatusQuery, String.class, checkAlreadyApplyStatusParams);
    }

    public int existMatchApply(int userIdxByJwt, int matchPostIdx) {
        String existMatchApplyQuery = "select count(*) from MatchApply where userIdx=? and matchPostIdx=?";
        Object []existMatchApplyParams = new Object[] {userIdxByJwt, matchPostIdx};
        return this.jdbcTemplate.queryForObject(existMatchApplyQuery, int.class, existMatchApplyParams);
    }

    public void cancelApplyTeamMatch(int userIdxByJwt, int matchPostIdx) {
        String cancelApplyTeamMatchQuery = "update MatchApply set status='CANCELED' where userIdx=? and matchPostIdx=? and status!= 'ACCEPTED'";
        Object [] cancelApplyTeamMatchParams = new Object[] {userIdxByJwt, matchPostIdx};
        this.jdbcTemplate.update(cancelApplyTeamMatchQuery, cancelApplyTeamMatchParams);
    }

    public String selectEndTime(int teamScheduleIdx) throws ParseException {
        String selectEndTimeQuery = "select endTime from teamSchedule where teamScheduleIdx=?";
        int selectEndTimeParams = teamScheduleIdx;
        return this.jdbcTemplate.queryForObject(selectEndTimeQuery, String.class, selectEndTimeParams);
    }

    public int selectHomeIdxByTeamScheduleIdx(int teamScheduleIdx) {
        String selectHomeIdxByTeamScheduleIdxQuery = "select homeIdx from teamSchedule where teamScheduleIdx=?";
        int selectHomeIdxByTeamScheduleIdxParams = teamScheduleIdx;
        return this.jdbcTemplate.queryForObject(selectHomeIdxByTeamScheduleIdxQuery, int.class, selectHomeIdxByTeamScheduleIdxParams);
    }

    public int selectAwayIdxByTeamScheduleIdx(int teamScheduleIdx) {
        String selectAwayIdxByTeamScheduleIdxQuery = "select awayIdx from teamSchedule where teamScheduleIdx=?";
        int selectAwayIdxByTeamScheduleIdxParams = teamScheduleIdx;
        return this.jdbcTemplate.queryForObject(selectAwayIdxByTeamScheduleIdxQuery, int.class, selectAwayIdxByTeamScheduleIdxParams);
    }

    public int existTeamInfo(int teamIdx) { // 탐 일정이 존재하지 않는 경우, 0 리턴
        String existTeamInfoQuery = "select count(*) from TeamInfo where teamIdx=?";
        int existTeamInfoParams = teamIdx;
        return this.jdbcTemplate.queryForObject(existTeamInfoQuery, int.class, existTeamInfoParams);
    }

    public int selectLeaderIdxByTeamIdx(int teamIdx) {
        String selectLeaderIdxByTeamIdxQuery = "select userIdx from User where teamIdx=? and isLeader='T'";
        int selectLeaderIdxByTeamIdxParams = teamIdx;
        return this.jdbcTemplate.queryForObject(selectLeaderIdxByTeamIdxQuery, int.class, selectLeaderIdxByTeamIdxParams);
    }

    public int postGameResult(PostGameResultReq postGameResultReq) {
        String postGameResultQuery = "INSERT INTO GameResult (teamScheduleIdx, homeScore, awayScore) VALUES (?,?,?)";
        Object []postGameResultParams = new Object[] {postGameResultReq.getTeamScheduleIdx(), postGameResultReq.getHomeScore(),postGameResultReq.getAwayScore()};
        this.jdbcTemplate.update(postGameResultQuery, postGameResultParams);

        String lastInsertIdxQuery = "select last_insert_id()";
        return jdbcTemplate.queryForObject(lastInsertIdxQuery, int.class);
    }

    public int selectTeamScoreByTeamIdx(int teamIdx) {
        String selectTeamScoreByTeamIdxQuery = "select teamScore from TeamInfo where teamIdx=?";
        int selectTeamScoreByTeamIdxParams = teamIdx;
        return this.jdbcTemplate.queryForObject(selectTeamScoreByTeamIdxQuery, int.class, selectTeamScoreByTeamIdxParams);
    }

    public void updateTeamScore(int homeIdx, int homeScore) {
        String updateTeamScoreQuery = "update TeamInfo set teamScore=? where teamIdx=?";
        Object [] updateTeamScoreParams = new Object[] {homeScore, homeIdx};
        this.jdbcTemplate.update(updateTeamScoreQuery, updateTeamScoreParams);
    }

    public List<GetApplyTeamRes> getApplyTeamRes(int teamScheduleIdx) {
        String getApplyTeamResQuery = "select T.teamIdx, T.name, T.town, T.teamScore, T.teamProfileImgUrl, T.introduce, MA.status\n" +
                "from MatchApply MA\n" +
                "join MatchPost MP on MA.matchPostIdx = MP.matchPostIdx\n" +
                "join User U on U.userIdx = MA.userIdx\n" +
                "join Teaminfo T on U.teamIdx = T.teamIdx\n" +
                "where MP.teamScheduleIdx=? and MP.type='TEAM'";
        return this.jdbcTemplate.query(getApplyTeamResQuery, (rs, rowNum) -> new GetApplyTeamRes(
                rs.getInt("teamIdx"),
                rs.getString("name"),
                rs.getString("town"),
                rs.getInt("teamScore"),
                rs.getString("teamProfileImgUrl"),
                rs.getString("introduce"),
                rs.getString("status")
        ), teamScheduleIdx);
    }

    public int selectUserIdxByTeamScheduleIdx(int teamScheduleIdx) {
        String selectUserIdxByTeamScheduleIdxQuery = "select U.userIdx\n" +
                "from teamSchedule T\n" +
                "join User U on U.teamIdx = T.homeIdx\n" +
                "where teamScheduleIdx=? and U.isLeader = 'T'";
        int selectUserIdxByTeamScheduleIdxParams = teamScheduleIdx;
        return this.jdbcTemplate.queryForObject(selectUserIdxByTeamScheduleIdxQuery, int.class, selectUserIdxByTeamScheduleIdxParams);
    }

    public int checkUserExist(int userIdx) {
        String checkUserExistQuery = "select exists(select email from User where userIdx = ?)";
        int checkUserExistParams = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserExistQuery, int.class, checkUserExistParams);
    }

    public List<GetTeamMatchPostRes> getTeamMatchPosts(int userIdx, String town, String startTime, String endTime) {
        String getTeamMatchPostsQuery = "select TS.teamScheduleIdx as teamScheduleIdx, \n" +
                "if(MP.userIdx=?, TRUE, FALSE) as isMyPost,\n" +
                "TI.name as homeTeamName,\n" +
                "TS.address as address,\n" +
                "TS.startTime as startTime,\n" +
                "TS.endTime as endTime,\n" +
                "TS.headCnt as headCnt,\n" +
                "MP.contents as contents,\n" +
                "MA.status as status\n" +
                "from MatchPost MP\n" +
                "join TeamSchedule TS on MP.teamScheduleIdx = TS.teamScheduleIdx\n" +
                "join TeamInfo TI on TS.homeIdx = TI.teamIdx\n" +
                "left join MatchApply MA on MA.matchPostIdx = MP.matchPostIdx\n" +
                "where type='TEAM' and startTime >= ? and endTime <= ? and locate(?, address)";
        return this.jdbcTemplate.query(getTeamMatchPostsQuery, (rs, rowNum) -> new GetTeamMatchPostRes(
                rs.getInt("teamScheduleIdx"),
                rs.getBoolean("isMyPost"),
                rs.getString("homeTeamName"),
                rs.getString("address"),
                rs.getString("startTime"),
                rs.getString("endTime"),
                rs.getInt("headCnt"),
                rs.getString("contents"),
                rs.getString("status")
        ), userIdx, startTime, endTime, town);
    }

    public int existMatchApplyIdx(int matchApplyIdx) {
        String existMatchApplyIdxQuery = "select count(*) from MatchApply where matchApplyIdx=?";
        int existMatchApplyIdxParams = matchApplyIdx;
        return this.jdbcTemplate.queryForObject(existMatchApplyIdxQuery, int.class, existMatchApplyIdxParams);
    }

    public int selectHomeLeaderIdxByMatchApplyIdx(int matchApplyIdx) {
        String selectUserIdxByMatchApplyIdxQuery = "select MP.userIdx\n" +
                "from MatchApply MA\n" +
                "join MatchPost MP on MA.matchPostIdx = MP.matchPostIdx\n" +
                "where matchApplyIdx=?";
        int selectUserIdxByMatchApplyIdxParams = matchApplyIdx;
        return this.jdbcTemplate.queryForObject(selectUserIdxByMatchApplyIdxQuery, int.class, selectUserIdxByMatchApplyIdxParams);
    }

    public String checkMatchApplyIdxStatus(int matchApplyIdx) {
        String checkAlreadyMatchApplyIdxQuery = "select status from MatchApply where matchApplyIdx = ?";
        int checkAlreadyMatchApplyIdxParams = matchApplyIdx;
        return this.jdbcTemplate.queryForObject(checkAlreadyMatchApplyIdxQuery, String.class, checkAlreadyMatchApplyIdxParams);
    }

    public int selectMatchPostIdxByMatchApplyIdx(int matchApplyIdx) {
        String selectMatchPostIdxByMatchApplyIdxQuery = "select matchPostIdx from MatchApply where matchApplyIdx=?";
        int selectMatchPostIdxByMatchApplyIdxParams = matchApplyIdx;
        return this.jdbcTemplate.queryForObject(selectMatchPostIdxByMatchApplyIdxQuery, int.class, selectMatchPostIdxByMatchApplyIdxParams);
    }

    public int selectAwayLeaderIdxByMatchApplyIdx(int matchApplyIdx) {
        String selectAwayLeaderIdxByMatchApplyIdxQuery = "select userIdx from MatchApply where matchApplyIdx=?";
        int selectAwayLeaderIdxByMatchApplyIdxParams = matchApplyIdx;
        return this.jdbcTemplate.queryForObject(selectAwayLeaderIdxByMatchApplyIdxQuery, int.class, selectAwayLeaderIdxByMatchApplyIdxParams);
    }

    public int selectTeamIdxByUserIdx(int userIdx) {
        String selectTeamIdxByUserIdxQuery = "select teamIdx from User where userIdx=?";
        int selectTeamIdxByUserIdxParams = userIdx;
        return this.jdbcTemplate.queryForObject(selectTeamIdxByUserIdxQuery, int.class, selectTeamIdxByUserIdxParams);
    }

    public void acceptTeamMatchApply(int matchApplyIdx, int matchPostIdx, int teamScheduleIdx, int awayIdx) {
        // 1. 승인한 MatchApply의 status를 ACCEPTED로 바꿔주기
        String acceptTeamMatchApplyQuery1 = "update MatchApply set status='ACCEPTED' where matchApplyIdx=?";
        int acceptTeamMatchApplyParams1 = matchApplyIdx;
        this.jdbcTemplate.update(acceptTeamMatchApplyQuery1, acceptTeamMatchApplyParams1);

        // 2. 해당 MatchPost에 대한 나머지 MatchApply의 status를 자동으로 DENIED 해주기
        String acceptTeamMatchApplyQuery2 = "update MatchApply set status='DENIED' where matchPostIdx=? and matchApplyIdx!=?";
        Object [] acceptTeamMatchApplyParams2 = new Object[] {matchPostIdx, matchApplyIdx};
        this.jdbcTemplate.update(acceptTeamMatchApplyQuery2, acceptTeamMatchApplyParams2);

        // 3. home팀의 TeamSchedule의 awayIdx 수정해주기
        String acceptTeamMatchApplyQuery3 = "update TeamSchedule set awayIdx=? where teamScheduleIdx=?";
        Object [] acceptTeamMatchApplyParams3 = new Object[] {awayIdx, teamScheduleIdx};
        this.jdbcTemplate.update(acceptTeamMatchApplyQuery3, acceptTeamMatchApplyParams3);

        // 4. away팀의 TeamSchedule 추가해주기
        String acceptTeamMatchApplyQuery4 = "insert into TeamSchedule(homeIdx, address, awayIdx, startTime, endTime, date, headCnt)\n" +
                "select awayIdx, address, homeIdx, startTime, endTime, date, headCnt \n" +
                "from TeamSchedule\n" +
                "where teamScheduleIdx=?";
        int acceptTeamMatchApplyParams4 = teamScheduleIdx;
        this.jdbcTemplate.update(acceptTeamMatchApplyQuery4, acceptTeamMatchApplyParams4);
    }

    public void rejectTeamMatchApply(int matchApplyIdx) {
        String rejectTeamMatchApplyQuery = "update MatchApply set status='DENIED' where matchApplyIdx=?";
        int rejectTeamMatchApplyParams = matchApplyIdx;
        this.jdbcTemplate.update(rejectTeamMatchApplyQuery, rejectTeamMatchApplyParams);
    }
}
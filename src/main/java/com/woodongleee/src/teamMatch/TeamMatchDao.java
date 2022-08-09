package com.woodongleee.src.teamMatch;

import com.woodongleee.src.teamMatch.model.ModifyTeamMatchPostsReq;
import com.woodongleee.src.teamMatch.model.PostTeamMatchPostsReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.text.ParseException;

@Repository
public class TeamMatchDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 팀 매칭 글 작성하기
    public int insertMatchPost(int userIdx, PostTeamMatchPostsReq postTeamMatchPostsReq) {
        String insertMatchPostQuery = "INSERT INTO MatchPost(userIdx, teamScheduleIdx, contents, type) VALUES (?,?,?,?)";
        Object []insertMatchPostParams = new Object[] {userIdx, postTeamMatchPostsReq.getTeamScheduleIdx(),postTeamMatchPostsReq.getContents(),"TEAM"};
        this.jdbcTemplate.update(insertMatchPostQuery, insertMatchPostParams);

        String lastInsertIdxQuery = "select last_insert_id()";

        return jdbcTemplate.queryForObject(lastInsertIdxQuery, int.class);
    }

    // teamScheduleIdx를 사용해서 팀 매칭 글이 존재하는지 확인하기
    public int existTeamMatchPost(int teamScheduleIdx) {    // 해당 경기의 팀 매칭글의 개수 리턴
        String existTeamMatchPostQuery = "select count(*) from MatchPost where teamScheduleIdx = ?";
        int existTeamMatchPostParams = teamScheduleIdx;
        return this.jdbcTemplate.queryForObject(existTeamMatchPostQuery, int.class, existTeamMatchPostParams);
    }

    // 사용자가 탈퇴한 회원인지 확인하기
    public String checkUserStatus(int userIdx) {
        String checkUserStatusQuery = "select status from User where userIdx=?";
        int checkUserStatusParams = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserStatusQuery, String.class, checkUserStatusParams);
    }

    // 사용자가 리더인지 확인하기
    public String isLeader(int userIdx) {  // 사용자가 리더가 아닌 경우, 0 리턴
        String isLeaderQuery = "select isLeader from User where userIdx=?";
        int isLeaderParams = userIdx;
        return this.jdbcTemplate.queryForObject(isLeaderQuery, String.class, isLeaderParams);
    }

    // 팀 일정이 존재하는지 확인하기
    public int existTeamMatch(int teamScheduleIdx) {    // 탐 일정이 존재하지 않는 경우, 0 리턴
        String existTeamMatchQuery = "select count(*) from teamSchedule where teamScheduleIdx=?";
        int existTeamMatchParams = teamScheduleIdx;
        return this.jdbcTemplate.queryForObject(existTeamMatchQuery, int.class, existTeamMatchParams);
    }

    // 팀 매칭 글 작성 기간 확인하기
    public String selectStartTime(int teamScheduleIdx) throws ParseException {   // 팀 매칭 글 작성 기간이 지난 경우, 0 리턴
        String selectStartTimeQuery = "select startTime from teamSchedule where teamScheduleIdx=?";
        int selectStartTimeParams = teamScheduleIdx;
        return this.jdbcTemplate.queryForObject(selectStartTimeQuery, String.class, selectStartTimeParams);
    }

    // 팀 매칭글 작성자 확인하기
    public int whoWritePost(int matchPostIdx) {
        String whoWritePostQuery = "select userIdx from MatchPost where matchPostIdx=?";
        int whoWritePostParams = matchPostIdx;
        return this.jdbcTemplate.queryForObject(whoWritePostQuery, int.class, whoWritePostParams);
    }

    // matchPostIdx를 사용해서 팀 매칭 글이 존재하는지 확인하기
    public int existTeamMatchPostIdx(int matchPostIdx) {
        String existTeamMatchPostIdxQuery = "select count(*) from MatchPost where matchPostIdx = ?";
        int existTeamMatchPostIdxParams = matchPostIdx;
        return this.jdbcTemplate.queryForObject(existTeamMatchPostIdxQuery, int.class, existTeamMatchPostIdxParams);
    }

    // 팀 매칭글 수정하기
    public int updateMatchPost(int matchPostIdx, String contents) {
        String updateMatchPostQuery = "update MatchPost set contents=? where matchpostIdx=?";
        Object [] updateMatchPostParams = new Object[] {contents, matchPostIdx};
        return this.jdbcTemplate.update(updateMatchPostQuery, updateMatchPostParams);
    }
}

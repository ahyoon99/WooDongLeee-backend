package com.woodongleee.src.teamMatch;

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

    // 팀 매칭 글이 존재하는지 확인하기
    public int existTeamMatchPost(PostTeamMatchPostsReq postTeamMatchPostsReq) {    // 해당 경기의 팀 매칭글의 개수 리턴
        String existTeamMatchPostQuery = "select count(*) from MatchPost where teamScheduleIdx = ?";
        int existTeamMatchPostParams = postTeamMatchPostsReq.getTeamScheduleIdx();
        return this.jdbcTemplate.queryForObject(existTeamMatchPostQuery, int.class, existTeamMatchPostParams);
    }

    // 사용자가 리더인지 확인하기
    public String isLeader(int userIdx) {  // 사용자가 리더가 아닌 경우, 0 리턴
        String isLeaderQuery = "select isLeader from User where userIdx=?";
        int isLeaderParams = userIdx;
        return this.jdbcTemplate.queryForObject(isLeaderQuery, String.class, isLeaderParams);
    }

    // 팀 일정이 존재하는지 확인하기
    public int existTeamMatch(PostTeamMatchPostsReq postTeamMatchPostsReq) {    // 탐 일정이 존재하지 않는 경우, 0 리턴
        String existTeamMatchQuery = "select count(*) from teamSchedule where teamScheduleIdx=?";
        int existTeamMatchParams = postTeamMatchPostsReq.getTeamScheduleIdx();
        return this.jdbcTemplate.queryForObject(existTeamMatchQuery, int.class, existTeamMatchParams);
    }

    // 팀 매칭 글 작성 기간 확인하기
    public String checkPostPeriod(PostTeamMatchPostsReq postTeamMatchPostsReq) throws ParseException {   // 팀 매칭 글 작성 기간이 지난 경우, 0 리턴
        String checkPostPeriodQuery = "select startTime from teamSchedule where teamScheduleIdx=?";
        int checkPostPeriodParams = postTeamMatchPostsReq.getTeamScheduleIdx();
        return this.jdbcTemplate.queryForObject(checkPostPeriodQuery, String.class, checkPostPeriodParams);
    }
}

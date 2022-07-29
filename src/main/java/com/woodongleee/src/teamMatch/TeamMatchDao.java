package com.woodongleee.src.teamMatch;

import com.woodongleee.src.teamMatch.model.PostTeamMatchPostsReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
    public int isLeader(int userIdx) {  // 사용자가 리더가 아닌 경우, 0 리턴
        String isLeaderQuery = "select isLeader from User where userIdx=?";
        int isLeaderParams = userIdx;
        String result = this.jdbcTemplate.queryForObject(isLeaderQuery, String.class, isLeaderParams);
        if(result.equals("T")){ // T가 리턴 -> 리더임
            return 1;
        }
        else{
            return 0;
        }
    }

    // 팀 일정이 존재하는지 확인하기
    public int existTeamMatch(PostTeamMatchPostsReq postTeamMatchPostsReq) {    // 탐 일정이 존재하지 않는 경우, 0 리턴
        String existTeamMatchQuery = "select count(*) from teamSchedule where teamScheduleIdx=?";
        int existTeamMatchParams = postTeamMatchPostsReq.getTeamScheduleIdx();
        return this.jdbcTemplate.queryForObject(existTeamMatchQuery, int.class, existTeamMatchParams);
    }

    // 팀 매칭 글 작성 기간 확인하기
    public int checkPostPeriod(PostTeamMatchPostsReq postTeamMatchPostsReq) throws ParseException {   // 팀 매칭 글 작성 기간이 지난 경우, 0 리턴
        String checkPostPeriodQuery = "select startTime from teamSchedule where teamScheduleIdx=?";
        int checkPostPeriodParams = postTeamMatchPostsReq.getTeamScheduleIdx();
        String start = this.jdbcTemplate.queryForObject(checkPostPeriodQuery, String.class, checkPostPeriodParams);

        SimpleDateFormat formatedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 경기 시작 시간 세팅하기
        Date startDate = formatedTime.parse(start);
        Calendar startTime = Calendar.getInstance();
        startTime.setTime(startDate);

        startTime.add(Calendar.HOUR, -1);
        startTime.add(Calendar.MINUTE, -59);

        // 현재 시간 세팅하기
        Date date = new Date();
        Calendar now = Calendar.getInstance();
        now.setTime(date);

        if(startTime.compareTo(now)==-1){
            return 0;
        }
        else{
            return 1;
        }
    }
}

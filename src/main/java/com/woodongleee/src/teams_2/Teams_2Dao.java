package com.woodongleee.src.teams_2;

import com.woodongleee.src.teams_2.model.AcceptUserRes;
import com.woodongleee.src.teams_2.model.AddTeamScheduleReq;
import com.woodongleee.src.teams_2.model.TeamApplyListRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class Teams_2Dao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate=new JdbcTemplate(dataSource);
    }

    public int checkTeamIdxExist(int teamIdx){
        String checkTeamIdxExistQuery="SELECT exists(select teamIdx from TeamInfo where teamIdx=? and status='ACTIVE');";
        return this.jdbcTemplate.queryForObject(checkTeamIdxExistQuery, int.class, teamIdx);
    }

    public int isLeader(int userIdx){
        String Query = "select case isLeader\n" +
                "    when 'T' then 1\n" +
                "    else -1\n" +
                "end\n" +
                "from User\n" +
                "where userIdx=?;";

        return this.jdbcTemplate.queryForObject(Query, int.class, userIdx);
    }

    public int isOurTeam(int userIdx, int teamIdx) {
        String Query = "select if((select U.teamIdx from User U where userIdx=?)=? ,1, -1);";
        Object[] Params = new Object[] {userIdx, teamIdx};
        return this.jdbcTemplate.queryForObject(Query, int.class, Params);
    }
    public int changeTeamRecruit(int teamIdx) {
        String Query = "Update TeamInfo TI\n" +
                "Set isRecruiting=if(isRecruiting='TRUE', 'FALSE', 'TRUE')\n" +
                "where teamIdx=?;";
        this.jdbcTemplate.update(Query, teamIdx);

        String Query2 = "select if((select isRecruiting from TeamInfo where teamIdx=?)='TRUE', 1, -1)";

        return this.jdbcTemplate.queryForObject(Query2, int.class, teamIdx);
    }

    public void addTeamSchedule(AddTeamScheduleReq addTeamScheduleReq, int teamIdx) {
        String Query = "insert into TeamSchedule(homeIdx, address, startTime, endTime, date, headCnt) values(?,?,?,?,?,?);";
        Object[] Params = new Object[] {teamIdx, addTeamScheduleReq.getAddress(), addTeamScheduleReq.getStartTime(),
                addTeamScheduleReq.getEndTime(), addTeamScheduleReq.getDate(), addTeamScheduleReq.getHeadCnt()};

        this.jdbcTemplate.update(Query, Params);
    }

    public int checkTimeOfSchedule(String startTime, String endTime) {
        String Query = "select exists(select * from TeamSchedule where startTime >=? and endTime <=?);";
        Object[] Params = new Object[] {startTime, endTime};

        return this.jdbcTemplate.queryForObject(Query, int.class, Params);
    }

    public void disbandTeam(int teamIdx) {

        // {팀 일정, 유저 일정, 팀의 포스트, 포스트에 대한 신청} 삭제
        this.jdbcTemplate.execute("SET foreign_key_checks  = 0;");
        this.jdbcTemplate.update("delete TS, US, MP, MA\n" +
                "FROM TeamSchedule as TS\n" +
                "left join UserSchedule US on TS.teamScheduleIdx = US.teamScheduleIdx\n" +
                "left join MatchPost MP on TS.teamScheduleIdx = MP.teamScheduleIdx\n" +
                "left join MatchApply MA on MP.matchPostIdx = MA.matchPostIdx\n" +
                "where homeIdx=?;", teamIdx);
        this.jdbcTemplate.execute("SET foreign_key_checks  = 1;");

        // 유저 정보와 팀 신청 내역 업데이트
        this.jdbcTemplate.update("update User set teamIdx=null, isLeader='F' where teamIdx=?;", teamIdx);
        this.jdbcTemplate.update("update TeamApply set status='DENIED' where teamIdx=?;", teamIdx);
        this.jdbcTemplate.update("update TeamInfo set status='INACTIVE' where teamIdx=?;", teamIdx);
    }

    public int getTeamIdx(int userIdx) {
        String Query = "select teamIdx from User where userIdx=?;";
        return this.jdbcTemplate.queryForObject(Query, int.class, userIdx);
    }

    public void dropUser(int userIdx) {
        // 탈퇴 이전에 참여한 팀 경기에 대해서는 계속 참여 상태로 유지됩니다.
        String Query = "update User set teamIdx=null where userIdx=?";
        this.jdbcTemplate.update(Query, userIdx);

    }

    public void acceptUser(int teamApplyIdx) {
        String Query1 = "update TeamApply set status='ACCEPTED' where teamApplyIdx=?;"; // 팀 신청 수락
        String Query2 = "select userIdx, teamIdx from TeamApply where teamApplyIdx=?;"; // TeamApply 테이블에서 유저, 팀 정보 가져오기
        this.jdbcTemplate.update(Query1, teamApplyIdx);
        AcceptUserRes acceptUserRes = this.jdbcTemplate.queryForObject(Query2, ((rs, rowNum) -> new AcceptUserRes(
                rs.getInt("userIdx"),
                rs.getInt("teamIdx")
        )), teamApplyIdx);

        Object[] Params = new Object[]{acceptUserRes.getTeamIdx(), acceptUserRes.getUserIdx()};
        String Query3 = "update User set teamIdx=? where userIdx=?;"; // 유저 정보 갱신
        this.jdbcTemplate.update(Query3, Params);
    }

    public void rejectUser(int teamApplyIdx) {
        String Query = "update TeamApply set status='DENIED' where teamApplyIdx=?;";
        this.jdbcTemplate.update(Query, teamApplyIdx);
    }

    public String getTeamApplyStatus(int teamApplyIdx) {
        String Query = "select status from TeamApply where teamApplyIdx=?;";
        return this.jdbcTemplate.queryForObject(Query, String.class, teamApplyIdx);
    }

    public List<TeamApplyListRes> getTeamApplyList(int teamIdx) {
        String Query = "select teamApplyIdx, userIdx, status from TeamApply where teamIdx=?;";
        return this.jdbcTemplate.query(Query, (rs, rowNum) ->
                new TeamApplyListRes(
                        rs.getInt("teamApplyIdx"),
                        rs.getInt("userIdx"),
                        rs.getString("status")), teamIdx);
    }
}

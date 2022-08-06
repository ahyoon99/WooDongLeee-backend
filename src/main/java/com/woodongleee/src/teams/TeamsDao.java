package com.woodongleee.src.teams;

import com.woodongleee.src.teams.model.GetTeamsinfoRes;
import com.woodongleee.src.teams.model.GetUserInfoRes;
import com.woodongleee.src.teams.model.GetTeamScheduleInfoRes;
import com.woodongleee.src.teams.model.GetTeamsScheduleRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class TeamsDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate=new JdbcTemplate(dataSource);
    }

    public int checkTeamIdxExist(int teamIdx){
        String checkTeamIdxExistQuery="SELECT exists(select teamIdx from TeamInfo where teamIdx=?);";
        return this.jdbcTemplate.queryForObject(checkTeamIdxExistQuery, int.class, teamIdx);
    }
    public int checkTeamScheduleIdxExist(int teamScheduleIdx){
        String query="SELECT exists(select teamScheduleIdx from TeamSchedule where teamScheduleIdx=?);";
        return this.jdbcTemplate.queryForObject(query, int.class, teamScheduleIdx);
    }
    public int checkTeamNameExist(String name){
        String query="SELECT exists(select name from TeamInfo where name=?);";
        return this.jdbcTemplate.queryForObject(query, int.class, name);
    }
    public int checkTeamExist(int userIdx){
        String query="select if(teamIdx is null, -1, teamIdx) as teamIdx from User where userIdx=?;";
        return this.jdbcTemplate.queryForObject(query, int.class, userIdx);
    }

    public int checkApply(int teamIdx){
        String query="SELECT exists(select teamIdx from TeamInfo where teamIdx=? and isRecruiting='true');";
        return this.jdbcTemplate.queryForObject(query, int.class, teamIdx);
    }
    public int checkStatus(int userIdx, int teamIdx){
        String checkStatusQuery="SELECT exists(select teamApplyIdx from TeamApply where userIdx=? and teamIdx=? and status='ACCEPTED');";
        Object[] params= new Object[]{userIdx, teamIdx};
        return this.jdbcTemplate.queryForObject(checkStatusQuery, int.class, params);
    }
    public int checkApplyExist(int userIdx, int teamIdx){
        String checkQuery="SELECT exists(select teamApplyIdx from TeamApply where userIdx=? and teamIdx=?);";
        Object[] Params= new Object[]{userIdx, teamIdx};
        return this.jdbcTemplate.queryForObject(checkQuery, int.class, Params);
    }
    public List<GetTeamsinfoRes> getTeaminfoByTown(String town){
        String selectTeamsinfoquery="SELECT * from TeamInfo where LOCATE(?, town)>0";
        return this.jdbcTemplate.query(selectTeamsinfoquery, (rs, rowNum) -> new GetTeamsinfoRes(
                rs.getInt("teamIdx"),
                rs.getString("name"),
                rs.getString("town"),
                rs.getInt("teamScore"),
                rs.getString("teamProfileImgURL"),
                rs.getString("introduce"),
                rs.getString("isRecruiting"),
                rs.getString("status")
        ), town);
    }

    public GetTeamsinfoRes getTeaminfoByName(String name){
        String getTeamsByNameQuery="SELECT * from TeamInfo where name=?";
        return this.jdbcTemplate.queryForObject(getTeamsByNameQuery, (rs, rowNum)->new GetTeamsinfoRes(
                rs.getInt("teamIdx"),
                rs.getString("name"),
                rs.getString("town"),
                rs.getInt("teamScore"),
                rs.getString("teamProfileImgURL"),
                rs.getString("introduce"),
                rs.getString("isRecruiting"),
                rs.getString("status")
        ), name);
    }

    public GetTeamsinfoRes getTeaminfo(int teamIdx){
        String getTeaminfoQuery="SELECT * from TeamInfo where teamIdx=?";
        return this.jdbcTemplate.queryForObject(getTeaminfoQuery, (rs, rowNum)->new GetTeamsinfoRes(
                rs.getInt("teamIdx"),
                rs.getString("name"),
                rs.getString("town"),
                rs.getInt("teamScore"),
                rs.getString("teamProfileImgURL"),
                rs.getString("introduce"),
                rs.getString("isRecruiting"),
                rs.getString("status")
        ), teamIdx);
    }
    public List<GetTeamsScheduleRes> getTeamsScheduleRes(int teamIdx, String starDate, String endDate){
        String getTeamsScheduleQuery="SELECT teamIdx, (SELECT name from TeamInfo where TS.awayIdx=TeamInfo.teamIdx) as awayName, address, startTime, endTime, date, headCnt, joinCnt, userMatchCnt\n"
                +"from TeamSchedule TS join TeamInfo TI on TS.homeIdx=TI.teamIdx where teamIdx=? and date between ? and ?;";
        Object[] Params= new Object[]{teamIdx, starDate, endDate};
        return this.jdbcTemplate.query(getTeamsScheduleQuery, (rs, rowNum)->new GetTeamsScheduleRes(
                rs.getInt("teamIdx"),
                rs.getString("awayName"),
                rs.getString("address"),
                rs.getString("startTime"),
                rs.getString("endTime"),
                rs.getString("date"),
                rs.getInt("headCnt"),
                rs.getInt("joinCnt"),
                rs.getInt("userMatchCnt")
        ), Params);
    }
    public GetTeamScheduleInfoRes getTeamScheduleInfoRes(int teamScheduleIdx){
        String query="SELECT * from TeamSchedule where teamScheduleIdx=?;";
        return this.jdbcTemplate.queryForObject(query, (rs, rowNum)-> new GetTeamScheduleInfoRes(
                rs.getInt("teamScheduleIdx"),
                rs.getInt("homeIdx"),
                rs.getString("address"),
                rs.getInt("awayIdx"),
                rs.getString("startTime"),
                rs.getString("endTime"),
                rs.getString("date"),
                rs.getInt("headCnt"),
                rs.getInt("joinCnt"),
                rs.getInt("userMatchCnt")
        ), teamScheduleIdx);
    }

    public List<GetUserInfoRes> getUserInfoRes(int teamIdx){
        String query="SELECT userIdx, name, email, id, profileImgUrl, town, introduce, gender, age from User where teamIdx=? and status='ACTIVE';";
        return this.jdbcTemplate.query(query, (rs, rowNum)-> new GetUserInfoRes(
                rs.getInt("userIdx"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("id"),
                rs.getString("profileImgUrl"),
                rs.getString("town"),
                rs.getString("introduce"),
                rs.getString("gender"),
                rs.getInt("age")
        ), teamIdx);
    }

    public void teamApply(int userIdx, int teamIdx){
        String teamApplyquery="INSERT INTO TeamApply(userIdx, teamIdx) values(?, ?);";
        Object[] params=new Object[]{userIdx, teamIdx};
        this.jdbcTemplate.update(teamApplyquery, params);
    }
    public void cancelTeamApply(int userIdx, int teamIdx){
        String cancelQuery="UPDATE TeamApply SET status='CANCELED' where userIdx=? and teamIdx=?";
        Object[] Params= new Object[]{userIdx, teamIdx};
        this.jdbcTemplate.update(cancelQuery, Params);
    }
    public void leaveTeam(int userIdx, int teamIdx){
        String leaveQuery="update User as U, TeamInfo as TI set U.teamIdx=null, TI.isRecruiting=\n"
        +"case when TI.isRecruiting='FALSE' then 'TRUE' ELSE 'TRUE' end where U.teamIdx=TI.teamIdx and U.userIdx=? and U.teamIdx=?;";
        Object[] Params= new Object[]{userIdx, teamIdx};
        this.jdbcTemplate.update(leaveQuery, Params);
    }

    public List<GetTeamsinfoRes> getTeamsByRankRes(String town){
        String query="SELECT * from TeamInfo WHERE LOCATE(?, town)>0 and status='ACTIVE' ORDER BY teamScore DESC;";
        return this.jdbcTemplate.query(query, (rs, rowNum)->new GetTeamsinfoRes(
                rs.getInt("teamIdx"),
                rs.getString("name"),
                rs.getString("town"),
                rs.getInt("teamScore"),
                rs.getString("teamProfileImgURL"),
                rs.getString("introduce"),
                rs.getString("isRecruiting"),
                rs.getString("status")
        ), town);
    }
}

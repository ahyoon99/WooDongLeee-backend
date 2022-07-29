package com.woodongleee.teams;

import com.woodongleee.teams.model.GetTeamsinfoRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

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
    public GetTeamsinfoRes getTeaminfoByTown(String town){
        String selectTeamsinfoquery="SELECT * from TeamInfo where town=?";
        return this.jdbcTemplate.queryForObject(selectTeamsinfoquery, (rs, rowNum) -> new GetTeamsinfoRes(
                rs.getInt("teamIdx"),
                rs.getString("name"),
                rs.getString("town"),
                rs.getInt("teamScore"),
                rs.getString("teamProfileImgURL"),
                rs.getString("introduce"),
                rs.getBoolean("isRecruiting"),
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
                rs.getBoolean("isRecruiting"),
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
                rs.getBoolean("isRecruiting"),
                rs.getString("status")
        ), teamIdx);
    }

}

package com.woodongleee.teams;

import com.woodongleee.teams.model.GetTeamsinfoRes;
import com.woodongleee.teams.model.Teaminfo;
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

    public GetTeamsinfoRes getTeaminfoByTown(String town){
        String selectTeamsinfoquery="SELECT * from TeamInfo where town=?";
        return this.jdbcTemplate.queryForObject(selectTeamsinfoquery, (rs, rowNum) -> new GetTeamsinfoRes(
                rs.getInt("teamIdx"),
                rs.getString("name"),
                rs.getString("town"),
                rs.getInt("teamScore"),
                rs.getString("teamProfileImgURL"),
                rs.getBoolean("isRecruiting"),
                rs.getString("status")
        ), town);
    }

}

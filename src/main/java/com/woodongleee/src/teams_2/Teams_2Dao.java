package com.woodongleee.src.teams_2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class Teams_2Dao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate=new JdbcTemplate(dataSource);
    }

    public int checkTeamIdxExist(int teamIdx){
        String checkTeamIdxExistQuery="SELECT exists(select teamIdx from TeamInfo where teamIdx=?);";
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
}

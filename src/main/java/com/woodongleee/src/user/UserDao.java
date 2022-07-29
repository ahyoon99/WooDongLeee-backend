package com.woodongleee.src.user;

import com.woodongleee.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    public int createUser(CreateUserReq newUser) {
        String createUserQuery = "insert into User (name, age, gender, email, id, password, town) VALUES (?,?,?,?,?,?,?)";
        Object[] createUserParams = new Object[]{newUser.getName(), newUser.getAge(), newUser.getGender(), newUser.getEmail(), newUser.getId(), newUser.getPassword(), newUser.getTown()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    public int isIdDuplicated(String id) {
        String isIdDuplicatedQuery = "select exists(select id from User where id = ?)";
        return this.jdbcTemplate.queryForObject(isIdDuplicatedQuery,
                int.class,
                id);
    }

    public int isEmailDuplicated(String email) {
        String isEmailDuplicatedQuery = "select exists(select email from User where email = ?)";
        return this.jdbcTemplate.queryForObject(isEmailDuplicatedQuery,
                int.class,
                email);
    }

    public void createCode(String email, String code) {
        String createCodeQuery = "insert into emailcode (email, code, expirationTime) VALUES (?,?,DATE_ADD(NOW(), INTERVAL 5 MINUTE))";
        Object[] createCodeParams = new Object[]{email, code};
        this.jdbcTemplate.update(createCodeQuery, createCodeParams);
    }

    public VerifyDomain verify(String email) {
        String verifyCodeQuery = "select email, code, expirationTime from emailcode where email = ?";
        return this.jdbcTemplate.queryForObject(verifyCodeQuery, (rs, rowNum) -> new VerifyDomain(
                rs.getString("email"),
                rs.getString("code"),
                rs.getTimestamp("expirationTime")),
                email
        );
    }

    public int isEmailVerifyCodeRequestDuplicated(String email) {
        String isEmailVerifyCodeRequestDuplicatedQuery = "select exists(select email from emailcode where email = ?)";
        String checkEmailCodeRequestExistParams = email;
        return this.jdbcTemplate.queryForObject(isEmailVerifyCodeRequestDuplicatedQuery,
                int.class,
                checkEmailCodeRequestExistParams);
    }

    public int deleteDuplicatedEmail(String email) {
        String deleteDuplicatedEmailQuery = "delete from emailcode where email = ?";
        String deleteDuplicatedEmailParams = email;
        return this.jdbcTemplate.update(deleteDuplicatedEmailQuery, deleteDuplicatedEmailParams);
    }

    public UserLoginUserIdxAndPassword login(String id) {
        String userLoginQuery = "select userIdx, password from user where id = ?";
        return this.jdbcTemplate.queryForObject(userLoginQuery,(rs, rowNum) -> new UserLoginUserIdxAndPassword(
                        rs.getInt("userIdx"),
                        rs.getString("password")),
                id);
    }

    public GetUserByJwtRes getUserByJwt(int userIdx) {
        String getUserByJwtQuery = "select U.name, age, gender, email, id, U.town, U.introduce, T.name as teamName, T.teamProfileImgUrl, U.status\n" +
                "from user as U\n" +
                "left join teaminfo T on U.teamIdx = T.teamIdx\n" +
                "where U.userIdx = ?;";
        return this.jdbcTemplate.queryForObject(getUserByJwtQuery, (rs,rowNum) -> new GetUserByJwtRes(
                rs.getString("name"),
                rs.getInt("age"),
                rs.getString("gender"),
                rs.getString("email"),
                rs.getString("id"),
                rs.getString("town"),
                rs.getString("introduce"),
                rs.getString("teamName"),
                rs.getString("teamProfileImgUrl"),
                rs.getString("status")),
                userIdx
        );
    }
}


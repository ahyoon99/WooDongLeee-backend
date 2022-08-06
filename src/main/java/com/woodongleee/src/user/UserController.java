package com.woodongleee.src.user;

import com.woodongleee.config.BaseException;
import com.woodongleee.config.BaseResponse;
import com.woodongleee.src.email.EmailService;
import com.woodongleee.src.user.model.*;
import com.woodongleee.utils.JwtService;
import com.woodongleee.utils.ValidationRegex;
import org.hibernate.sql.Update;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.woodongleee.config.BaseResponseStatus.*;
import static com.woodongleee.utils.ValidationRegex.*;


@RestController
@RequestMapping("/users")
public class UserController {

    private final UserProvider userProvider;
    private final UserService userService;
    private final JwtService jwtService;
    private final EmailService emailService;

    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService, EmailService emailService){
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

    //회원가입
    @ResponseBody
    @PostMapping
    public BaseResponse<String> createUser(@RequestBody CreateUserReq newUser){
        Object[] params = new Object[]{
                newUser.getName(),
                newUser.getAge(),
                newUser.getGender(),
                newUser.getEmail(),
                newUser.getId(),
                newUser.getPassword(),
                newUser.getTown(),
                newUser.getCode()
        };
        if(Arrays.stream(params).anyMatch(Objects::isNull)){
            return new BaseResponse<>(EMPTY_PARAMETER);
        }
        if(!isRegexEmail(newUser.getEmail())){
            return new BaseResponse<>(INVALID_EMAIL_PATTERN);
        }
        if(!isRegexId(newUser.getId())){
            return new BaseResponse<>(INVALID_ID_PATTERN);
        }
        if(newUser.getPassword().length() < 8 || newUser.getPassword().length() > 20){
            return new BaseResponse<>(INVALID_PASSWORD_PATTERN);
        }

        try {
            userService.createUser(newUser);
            return new BaseResponse<>("회원가입에 성공하였습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    //id 중복 검사
    @ResponseBody
    @GetMapping("/is-duplicated/id")
    public BaseResponse<String> isIdDuplicated(@RequestParam String id){
        if(!isRegexId(id)){
            return new BaseResponse<>(INVALID_ID_PATTERN);
        }
        try {
            if(userProvider.isIdDuplicated(id)){
                return new BaseResponse<>(DUPLICATED_ID);
            }
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

        return new BaseResponse<>("중복 검사 성공");
    }

    //이메일 중복 검사 + 인증
    @Transactional
    @ResponseBody
    @GetMapping("/is-duplicated/email")
    public BaseResponse<String> isEmailDuplicated(@RequestParam String email){
        if(!isRegexEmail(email)){
            return new BaseResponse<>(INVALID_EMAIL_PATTERN);
        }
        try {
            if(userProvider.isEmailDuplicated(email)){
                return new BaseResponse<>(DUPLICATED_EMAIL);
            }
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
        try {
            String code = emailService.createKey();
            userService.createCode(email, code);
            emailService.sendSimpleMessage(email, code);
            return new BaseResponse<>("인증번호가 전송되었습니다.");
        } catch (Exception e) {
            return new BaseResponse<>(EMAIL_SERVER_ERROR);
        }

    }

    @ResponseBody
    @PostMapping("/login")
    public BaseResponse<UserLoginRes> login(@RequestBody UserLoginReq userLoginReq){
        Object[] params = new Object[]{
                userLoginReq.getId(),
                userLoginReq.getPassword()
        };
        if(Arrays.stream(params).anyMatch(Objects::isNull)){
            return new BaseResponse<>(EMPTY_PARAMETER);
        }

        try{
            return new BaseResponse<>(userProvider.login(userLoginReq));
        } catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @GetMapping()
    public BaseResponse<GetUserByJwtRes> getUserByJwt(){
        try {
            int userIdx = jwtService.getUserIdx();
            GetUserByJwtRes getUserByJwtRes = userProvider.getUserByJwt(userIdx);
            return new BaseResponse<>(getUserByJwtRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping()
    public BaseResponse<String> updateUser(@RequestBody UpdateUserReq updateUserReq){
        Object[] params = new Object[]{
                updateUserReq.getName(),
                updateUserReq.getAge(),
                updateUserReq.getGender(),
                updateUserReq.getTown(),
        };
        if(Arrays.stream(params).anyMatch(Objects::isNull)){
            return new BaseResponse<>(EMPTY_PARAMETER);
        }
        try{
            int userIdx = jwtService.getUserIdx();
            userService.updateUser(userIdx, updateUserReq);
            return new BaseResponse<>("수정이 완료되었습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping("/pwd")
    public BaseResponse<String> updatePassword(@RequestBody UpdatePasswordReq updatePasswordReq){
        String currentPassword = updatePasswordReq.getCurrentPassword();
        String newPassword = updatePasswordReq.getNewPassword();
        if(newPassword == null || currentPassword == null){
            return new BaseResponse<>(EMPTY_PARAMETER);
        }
        try {
            int userIdx = jwtService.getUserIdx();
            userService.updatePassword(userIdx, updatePasswordReq);
            return new BaseResponse<>("수정이 완료되었습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping("/id")
    public BaseResponse<String> updateId(@RequestBody UpdateIdReq updateIdReq){
        String id = updateIdReq.getId();
        if(id == null){
            return new BaseResponse<>(EMPTY_PARAMETER);
        }
        if(!isRegexId(id)){
            return new BaseResponse<>(INVALID_ID_PATTERN);
        }
        try{
            int userIdx = jwtService.getUserIdx();
            userService.updateId(userIdx, id);
            return new BaseResponse<>("수정이 완료되었습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/schedule")
    public BaseResponse<List<GetUserScheduleRes>> getUserSchedule() {
        try {
            int userIdx = jwtService.getUserIdx();
            List<GetUserScheduleRes> userScheduleList = userProvider.getUserSchedule(userIdx);
            return new BaseResponse<>(userScheduleList);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
    @ResponseBody
    @GetMapping("/find")
    public BaseResponse<GetIdByEmailRes> getIdByEmail(@RequestParam String email){
        try{
            GetIdByEmailRes getIdByEmailRes = userProvider.getIdByEmail(email);
            return new BaseResponse<>(getIdByEmailRes);
        } catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/apply")
    public BaseResponse<List<GetUserApplyRes>> getUserApply(){
        try{
            int userIdx = jwtService.getUserIdx();
            List<GetUserApplyRes> getUserApplyRes = userProvider.getUserApply(userIdx);
            return new BaseResponse<>(getUserApplyRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}

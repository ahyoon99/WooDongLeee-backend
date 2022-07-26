package com.woodongleee.src.user;

import com.woodongleee.config.BaseException;
import com.woodongleee.config.BaseResponse;
import com.woodongleee.src.email.EmailService;
import com.woodongleee.src.user.model.CreateUserReq;
import com.woodongleee.utils.JwtService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
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
        if(userProvider.isIdDuplicated(id)){
            return new BaseResponse<>(DUPLICATED_ID);
        }

        return new BaseResponse<>("중복 검사 성공");
    }

    //이메일 중복 검사 + 인증
    @Transactional
    @ResponseBody
    @GetMapping("/is-duplicated")
    public BaseResponse<String> isEmailDuplicated(@RequestParam String email){
        if(!isRegexEmail(email)){
            return new BaseResponse<>(INVALID_EMAIL_PATTERN);
        }
        if(userProvider.isEmailDuplicated(email)){
            return new BaseResponse<>(DUPLICATED_EMAIL);
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
}

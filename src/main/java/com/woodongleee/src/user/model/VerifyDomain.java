package com.woodongleee.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class VerifyDomain {
    private String email;
    private String code;
    private Timestamp expirationTime;
}

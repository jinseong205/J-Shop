package com.shop.server.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ExceptionCode {

    //User 관련
    DUPLICATE_EMAIL_USER_TO_CREATE(HttpStatus.CONFLICT, "이미 가입 된 회원 메일입니다."),
	NO_USER_TO_GET(HttpStatus.BAD_REQUEST, "해당 유저 정보가 없습니다."),
    NO_USER_TO_UPDATE(HttpStatus.BAD_REQUEST, "수정할 유저가 없습니다."),
    NO_USER_ROLE_TO_UPDATE(HttpStatus.BAD_REQUEST, "수정할 유저 권한 정보가 잘못되었습니다."),
    VALIDATION_USER_ID(HttpStatus.BAD_REQUEST, "아이디는 8자 이상 20자 이하로 입력하세요."),
    VALIDATION_USER_PASSWORD(HttpStatus.BAD_REQUEST, "패스워드는 8자 이상 20자 이하로 입력하세요."),
    VALIDATION_USER_ADDR(HttpStatus.BAD_REQUEST, "주소를 입력해주세요."),
    
    //Item 관련
    NO_ITEM_TO_UPDATE(HttpStatus.BAD_REQUEST, "수정할 상품이 없습니다."),
    
    //권한 오류
    PERMISSION_ERROR(HttpStatus.UNAUTHORIZED, "권한이 부족합니다.")
    
    ;


    @JsonIgnore
    private final HttpStatus httpStatus;
    private final String message;
}

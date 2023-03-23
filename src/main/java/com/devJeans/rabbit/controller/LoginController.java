package com.devJeans.rabbit.controller;

import com.devJeans.rabbit.bind.ApiResult;
import com.devJeans.rabbit.dto.IdTokenRequestDto;
import com.devJeans.rabbit.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

import static com.devJeans.rabbit.bind.ApiResult.succeed;

@RestController
@RequestMapping("/v1/oauth")
public class LoginController {

    @Autowired
    AccountService accountService;

    @PostMapping("/login")
    public ApiResult LoginWithGoogleOauth2(@RequestBody IdTokenRequestDto requestBody, HttpServletResponse response) {
        String authToken = accountService.loginOAuthGoogle(requestBody);
        final ResponseCookie cookie = ResponseCookie.from("AUTH-TOKEN", authToken)
                .httpOnly(true)
                .maxAge(7 * 24 * 3600)
                .path("/")
                .secure(false)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return succeed("JWT가 정상적으로 발급도었습니다.");
    }
}

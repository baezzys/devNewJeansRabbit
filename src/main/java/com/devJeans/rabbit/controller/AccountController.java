package com.devJeans.rabbit.controller;

import com.devJeans.rabbit.bind.ApiResult;
import com.devJeans.rabbit.domain.Account;
import com.devJeans.rabbit.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import static com.devJeans.rabbit.bind.ApiResult.succeed;
import static com.devJeans.rabbit.dto.AccountDto.of;

@RestController
@RequestMapping("/user")
public class AccountController {

    @Autowired
    AccountService accountService;

    @GetMapping("/info")
    public ApiResult getUserInfo(Principal principal) {
        Account account = accountService.getAccount(Long.valueOf(principal.getName()));
        return succeed(account);
    }

    @GetMapping("/photos")
    public ApiResult getAllPhotoOfUser(Principal principal) {
        Account account = accountService.getAccount(Long.valueOf(principal.getName()));

        return succeed(account.getPhotos());
    }
}

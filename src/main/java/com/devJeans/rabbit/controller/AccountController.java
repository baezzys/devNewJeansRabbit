package com.devJeans.rabbit.controller;

import com.devJeans.rabbit.domain.Account;
import com.devJeans.rabbit.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import static com.devJeans.rabbit.dto.AccountDto.of;

@RestController
@RequestMapping("/user")
public class AccountController {

    @Autowired
    AccountService accountService;

    @GetMapping("/info")
    public ResponseEntity getUserInfo(Principal principal) {
        Account account = accountService.getAccount(Long.valueOf(principal.getName()));
        return ResponseEntity.ok().body(of(account));
    }

    @GetMapping("/photos")
    public ResponseEntity getAllPhotoOfUser(Principal principal) {
        Account account = accountService.getAccount(Long.valueOf(principal.getName()));

        return ResponseEntity.ok(account.getPhotos());
    }
}

package com.devJeans.rabbit.controller;

import com.devJeans.rabbit.bind.ApiResult;
import com.devJeans.rabbit.domain.Account;
import com.devJeans.rabbit.dto.AccountDto;
import com.devJeans.rabbit.dto.PhotoDto;
import com.devJeans.rabbit.repository.AccountRepository;
import com.devJeans.rabbit.service.AccountService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import static com.devJeans.rabbit.bind.ApiResult.succeed;

@RestController
@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:5173", "https://devjeans.dev-hee.com", "https://www.devnewjeans.com", "https://stg-devjeans.dev-hee.com"},  allowCredentials = "true")
@RequestMapping("/user")
public class AccountController {

    private final AccountService accountService;

    private final AccountRepository accountRepository;

    public AccountController(AccountService accountService, AccountRepository accountRepository) {
        this.accountService = accountService;
        this.accountRepository = accountRepository;
    }

    @GetMapping("/info")
    @Transactional
    public ApiResult<AccountDto> getUserInfo(Principal principal) {
        Account account = accountService.getAccount(Long.valueOf(principal.getName()));
        return succeed(AccountDto.of(account));
    }

    @GetMapping("/photos")
    public ApiResult<List<PhotoDto>> getAllPhotoOfUser(Principal principal) {
        Account account = accountService.getAccount(Long.valueOf(principal.getName()));

        return succeed(account.getCreatedPhotos().stream().map(photo -> PhotoDto.of(photo)).collect(Collectors.toList()));
    }
}

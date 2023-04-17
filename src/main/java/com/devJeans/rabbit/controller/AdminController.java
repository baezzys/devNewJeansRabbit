package com.devJeans.rabbit.controller;

import com.devJeans.rabbit.bind.ApiResult;
import com.devJeans.rabbit.domain.Account;
import com.devJeans.rabbit.repository.AccountRepository;
import com.devJeans.rabbit.service.AccountService;
import com.devJeans.rabbit.service.PhotoService;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.devJeans.rabbit.bind.ApiResult.succeed;

@RestController
@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:5173", "https://devjeans.dev-hee.com", "https://www.devnewjeans.com", "https://stg-devjeans.dev-hee.com"},  allowCredentials = "true")
@RequestMapping("/admin")
public class AdminController {

    private final AccountService accountService;

    private final AccountRepository accountRepository;

    private final PhotoService photoService;


    public AdminController(AccountService accountService, AccountRepository accountRepository, PhotoService photoService) {
        this.accountService = accountService;
        this.accountRepository = accountRepository;
        this.photoService = photoService;
    }

    @PostMapping("/photo")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<String> hidePhoto(@RequestParam Long photoId) {
        photoService.hidePhoto(photoId);
        return succeed("사진이 숨김 처리 되었습니다. 사진 id : " + photoId);
    }
}

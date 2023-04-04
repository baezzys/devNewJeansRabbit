package com.devJeans.rabbit.controller;

import com.devJeans.rabbit.bind.ApiResult;
import com.devJeans.rabbit.domain.Account;
import com.devJeans.rabbit.repository.AccountRepository;
import com.devJeans.rabbit.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import static com.devJeans.rabbit.bind.ApiResult.succeed;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "https://devjeans.dev-hee.com", "https://www.devnewjeans.com"},  allowCredentials = "true")
@RequestMapping("/admin")
public class AdminController {

    AccountRepository accountRepository;

    PhotoService photoService;
    public AdminController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @DeleteMapping("/photo")
    public ApiResult<String> deletePhoto(Long userId, Long photoId) {
        Account adminUser = accountRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("해당 id에 존재하는 유저가 없습니다."));

        photoService.deletePhoto(userId, photoId);

        return succeed("사진이 정상적으로 삭제 되었습니다.");
    }
}

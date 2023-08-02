package com.devJeans.rabbit.dto;

import com.devJeans.rabbit.domain.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class AccountDto {

    public AccountDto(String email, String profileImage) {
        this.email = email;
        this.profileImage = profileImage;
    }

    private String email;

    private String profileImage;

    public static final AccountDto of(Account account) {
        return new AccountDto(account.getEmail(), account.getProfilePictureUrl());
    }
}

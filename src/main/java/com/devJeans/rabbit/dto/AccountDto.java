package com.devJeans.rabbit.dto;

import com.devJeans.rabbit.domain.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {

    private String email;

    private String profileImage;

    public static final AccountDto of(Account account) {
        return AccountDto.builder()
                .email(account.getEmail())
                .profileImage(account.getProfilePictureUrl())
                .build();
    }
}

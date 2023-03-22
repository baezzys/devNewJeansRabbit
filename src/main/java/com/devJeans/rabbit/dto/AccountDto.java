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

    private String firstName;

    private String lastName;

    private String email;

    public static final AccountDto of(Account account) {
        return AccountDto.builder()
                .firstName(account.getFirstName())
                .lastName(account.getLastName())
                .email(account.getEmail())
                .build();
    }
}

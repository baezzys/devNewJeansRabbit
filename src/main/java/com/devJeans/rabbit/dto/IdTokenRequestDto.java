package com.devJeans.rabbit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class IdTokenRequestDto {

    private String idToken;

    public IdTokenRequestDto(String idToken) {
        this.idToken = idToken;
    }

    public String getIdToken() {
        return idToken;
    }
}

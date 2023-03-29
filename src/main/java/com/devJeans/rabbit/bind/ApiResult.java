package com.devJeans.rabbit.bind;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.http.HttpStatus;

public class ApiResult<T>  {

    @Schema(description = "The result data")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T data;

    private final String message;
    private final int status;

    ApiResult(T data, String message, int status) {
        this.data = data;
        this.message = message;
        this.status = status;
    }


    public static <T> ApiResult<T> succeed(T data) {
        return new ApiResult<>(data, null, HttpStatus.OK.value());
    }

    public static ApiResult<?> failed(Throwable throwable) {
        return failed(throwable.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static ApiResult<?> failed(String message) {
        return failed(message, HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static ApiResult<?> failed(String message, int status) {
        return new ApiResult<>(null, message, status);
    }

    public boolean hasData() {
        return data != null;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("data", data)
                .append("message", message)
                .append("status", status)
                .toString();
    }

}

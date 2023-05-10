package com.yeshen.appcenter.domain.common;

import com.yeshen.appcenter.domain.enums.ResultCode;
import lombok.Data;

@Data
public class ResultVO<T> {

    private Integer code;

    private String message;

    private T data;

    public ResultVO() {
    }

    public ResultVO(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static <T> ResultVO<T> createSuccess() {
        return createSuccess(null);
    }

    public static <T> ResultVO<T> createSuccess(T data) {
        ResultVO<T> result = new ResultVO<>();
        result.setCode(ResultCode.SERVER_SUCCESS.getCode());
        result.setMessage(ResultCode.SERVER_SUCCESS.getMessage());
        result.setData(data);
        return result;
    }

    public static <T> ResultVO<T> createError(ResultCode resultCode) {
        ResultVO<T> result = new ResultVO<>();
        result.setCode(resultCode.getCode());
        result.setMessage(resultCode.getMessage());
        return result;
    }

    public static <T> ResultVO<T> createError(ResultCode resultCode, T data) {
        ResultVO<T> result = new ResultVO<>();
        result.setCode(resultCode.getCode());
        result.setMessage(resultCode.getMessage());
        result.setData(data);
        return result;
    }

    public static <T> ResultVO<T> createError(BusinessException e) {
        ResultVO<T> result = new ResultVO<>();
        result.setCode(e.getErrorCode());
        result.setMessage(e.getMessage());
        return result;
    }
}
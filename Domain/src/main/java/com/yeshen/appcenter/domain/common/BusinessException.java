package com.yeshen.appcenter.domain.common;

import com.yeshen.appcenter.domain.enums.ResultCode;

public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    protected int errorCode;
    /**
     * 错误信息
     */
    protected String errorMsg;

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.errorCode = resultCode.getCode();
        this.errorMsg = resultCode.getMessage();
    }

    public BusinessException(ResultCode resultCode, String message) {
        super(resultCode.getMessage());
        this.errorCode = resultCode.getCode();
        this.errorMsg = message;
    }

    public BusinessException(ResultCode resultCode, Throwable cause) {
        super(resultCode.getMessage(), cause);
        this.errorCode = resultCode.getCode();
        this.errorMsg = resultCode.getMessage();
    }


    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
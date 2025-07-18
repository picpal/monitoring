package com.picpal.framework.common.exception;

import com.picpal.framework.common.enums.ErrorType;

public class UnknownException extends SystemRuntimeException {

    private static final long serialVersionUID = -7431810328087316293L;

    private final static ErrorType ERROR_TYPE = ErrorType.UNKNOWN;

    public UnknownException() {
        super(ERROR_TYPE);
    }
}
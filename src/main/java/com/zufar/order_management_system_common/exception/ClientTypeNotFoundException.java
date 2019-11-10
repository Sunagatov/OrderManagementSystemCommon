package com.zufar.order_management_system_common.exception;

public class ClientTypeNotFoundException extends RuntimeException {

    public ClientTypeNotFoundException() {
        super();
    }

    public ClientTypeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientTypeNotFoundException(String message) {
        super(message);
    }

    public ClientTypeNotFoundException(Throwable cause) {
        super(cause);
    }
}

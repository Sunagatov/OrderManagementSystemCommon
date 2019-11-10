package com.zufar.order_management_system_common.exception;

public class OrderCategoryNotFoundException extends RuntimeException {

    public OrderCategoryNotFoundException() {
        super();
    }

    public OrderCategoryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrderCategoryNotFoundException(String message) {
        super(message);
    }

    public OrderCategoryNotFoundException(Throwable cause) {
        super(cause);
    }
}

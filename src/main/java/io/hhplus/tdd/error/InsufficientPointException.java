package io.hhplus.tdd.error;

public class InsufficientPointException extends RuntimeException{
    public static final String ERROR_MESSAGE = "잔액 부족입니다.";
    public InsufficientPointException() {
        super(ERROR_MESSAGE);
    }
}

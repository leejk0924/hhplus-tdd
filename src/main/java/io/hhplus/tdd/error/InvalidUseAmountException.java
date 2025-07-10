package io.hhplus.tdd.error;

public class InvalidUseAmountException extends IllegalArgumentException {
    public static final String ERROR_MESSAGE = "사용가능 금액은 1원 이상 1000000원 이하 입니다.";
    public InvalidUseAmountException() {
        super(ERROR_MESSAGE);
    }
}

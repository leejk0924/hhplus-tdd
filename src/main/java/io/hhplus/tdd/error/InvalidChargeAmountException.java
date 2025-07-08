package io.hhplus.tdd.error;

public class InvalidChargeAmountException extends IllegalArgumentException{
    public static String ERROR_MESSAGE = "최소 충전 금액은 1원 이상입니다.";
    public InvalidChargeAmountException() {
        super(ERROR_MESSAGE);
    }
}

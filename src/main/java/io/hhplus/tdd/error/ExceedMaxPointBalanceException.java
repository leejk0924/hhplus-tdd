package io.hhplus.tdd.error;

public class ExceedMaxPointBalanceException extends IllegalArgumentException {
    public static String ERROR_MESSAGE = "포인트 %d원을 충전하면 최대 포인트에서 %d원을 초과합니다.";
    public ExceedMaxPointBalanceException(long amount, long excessAmount) {
        super (String.format(ERROR_MESSAGE, amount, excessAmount));
    }
}

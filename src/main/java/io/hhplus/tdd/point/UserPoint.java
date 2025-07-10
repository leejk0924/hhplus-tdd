package io.hhplus.tdd.point;

import io.hhplus.tdd.error.ExceedMaxPointBalanceException;
import io.hhplus.tdd.error.InsufficientPointException;
import io.hhplus.tdd.error.InvalidChargeAmountException;
import io.hhplus.tdd.error.InvalidUseAmountException;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {
    public static final Long MAX_POINT= 1_000_000L;
    public static final Long MIN_POINT = 0L;
    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }
    public UserPoint charge(long amount) {
        long expectPoint = amount + point();
        if (MAX_POINT < expectPoint) {
            throw new ExceedMaxPointBalanceException(amount, (expectPoint - MAX_POINT));
        }
        if (MIN_POINT >= amount) {
            throw new InvalidChargeAmountException();
        }
        return new UserPoint(this.id, this.point+ amount, System.currentTimeMillis());
    }
    public UserPoint use(long usePoint) {
        if (usePoint < MIN_POINT || usePoint > MAX_POINT) {
            throw new InvalidUseAmountException();
        }
        if (point() < usePoint) {
            throw new InsufficientPointException();
        }
        return new UserPoint(id(), point() - usePoint, System.currentTimeMillis());
    }
}

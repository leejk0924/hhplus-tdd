package io.hhplus.tdd.point;

import java.util.Comparator;

public record PointHistory(
        long id,
        long userId,
        long amount,
        TransactionType type,
        long updateMillis
) implements Comparable<PointHistory> {
    @Override
    public int compareTo(PointHistory other) {
        return Long.compare(this.updateMillis, other.updateMillis);
    }
    public static final Comparator<PointHistory> BY_TIME_DESC = Comparator.comparingLong(PointHistory::updateMillis).reversed();
}
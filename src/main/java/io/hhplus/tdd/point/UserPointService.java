package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserPointService {
    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;
    private final TimeProvider timeProvider;
    public UserPoint chargePoint(long userId, long amount) {
        var userPoint = userPointTable.selectById(userId);
        var chargedPoint = userPoint.charge(amount);
        userPointTable.insertOrUpdate(chargedPoint.id(), chargedPoint.point());
        pointHistoryTable.insert(
                chargedPoint.id(),
                userPoint.point(),
                TransactionType.CHARGE,
                timeProvider.now()
        );
        return chargedPoint;
    }
    public UserPoint searchPoint(long userId) {
        return userPointTable.selectById(userId);
    }
    public UserPoint usePoint(long userId, long amount) {
        var userPoint = userPointTable.selectById(userId);
        var use = userPoint.use(amount);
        var usedUserPoint = userPointTable.insertOrUpdate(use.id(), use.point());
        pointHistoryTable.insert(
                usedUserPoint.id(),
                usedUserPoint.point(),
                TransactionType.USE,
                timeProvider.now()
        );
        return usedUserPoint;
    }
    public List<PointHistory> getPointHistory(long userId) {
        List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(userId);
        pointHistories.sort(PointHistory.BY_TIME_DESC);
        return pointHistories;
    }
}

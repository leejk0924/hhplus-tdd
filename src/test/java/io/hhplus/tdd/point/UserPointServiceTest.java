package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.time.TimeProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserPointServiceTest {
    @InjectMocks
    private UserPointService sut;
    @Mock
    private UserPointTable userPointTable;
    @Mock
    private PointHistoryTable pointHistoryTable;
    @Mock
    private TimeProvider timeProvider;

    @Test
    public void 포인트_충전_비즈니스영역_테스트() throws Exception {
        // Given
        given(userPointTable.selectById(anyLong()))
                .willAnswer(
                        i -> {
                            long inputId = i.getArgument(0);
                            return new UserPoint(inputId, 0, 0L);
                        }
                );
        given(userPointTable.insertOrUpdate(anyLong(), anyLong()))
                .willAnswer(
                        i -> {
                            long inputId = i.getArgument(0);
                            long inputAmount = i.getArgument(1);
                            return new UserPoint(inputId, inputAmount, 0L);
                        }
                );
        long inputId = 1L;
        long inputAmount = 2000L;
        // When
        UserPoint chargedPoint = sut.chargePoint(inputId, inputAmount);

        // Then
        verify(userPointTable).insertOrUpdate(anyLong(), anyLong());
        verify(userPointTable).selectById(anyLong());
        verify(pointHistoryTable).insert(anyLong(), anyLong(), any(TransactionType.class), anyLong());
        assertThat(chargedPoint.id()).isEqualTo(inputId);
        assertThat(chargedPoint.point()).isEqualTo(inputAmount);
    }
    @Test
    public void 포인트_조회_성공_케이스() throws Exception {
        // Given
        long initId = 1L;
        long initPoint = 2000L;
        given(userPointTable.selectById(anyLong())).willAnswer(
                i -> {
                    long id = i.getArgument(0);
                    return new UserPoint(id, initPoint, 0L);
                }
        );

        // When
        UserPoint searchPoint = sut.searchPoint(initId);

        // Then
        verify(userPointTable).selectById(anyLong());
        assertThat(searchPoint.id()).isEqualTo(initId);
        assertThat(searchPoint.point()).isEqualTo(initPoint);
    }
    @Test
    public void 포인트_사용_성공_케이스() throws Exception {
        // Given
        long initId = 1L;
        long initPoint = 1_000_000L;
        long amount = 1_000_000L;
        given(userPointTable.selectById(anyLong()))
                .willAnswer(
                        i -> {
                            long inputId = i.getArgument(0);
                            return new UserPoint(inputId, initPoint, 0L);
                        }
                );
        given(userPointTable.insertOrUpdate(anyLong(), anyLong()))
                .willAnswer(
                        i -> {
                            long inputId = i.getArgument(0);
                            long inputAmount = i.getArgument(1);
                            return new UserPoint(inputId, inputAmount, 0L);
                        }
                );
        // When
        UserPoint usedPoint = sut.usePoint(initId, amount);

        // Then
        verify(userPointTable).insertOrUpdate(anyLong(), anyLong());
        assertThat(usedPoint.id()).isEqualTo(initId);
        assertThat(usedPoint.point()).isEqualTo(initPoint - amount);
    }
}
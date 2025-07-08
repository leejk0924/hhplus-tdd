package io.hhplus.tdd.point;

import io.hhplus.tdd.error.ExceedMaxPointBalanceException;
import io.hhplus.tdd.error.InvalidChargeAmountException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class UserPointTest {
    @Nested
    @DisplayName("포인트 충전 테스트")
    class chargeTest {
        @ParameterizedTest
        @MethodSource("UserPoint_have_0_input_data")
        public void 포인트_충전_In_Range_성공_케이스(long id, long amount) throws Exception {
            // Given
            UserPoint initUser = UserPoint.empty(id);
            // When
            var chargedPoint = initUser.charge(amount);
            // Then
            assertThat(chargedPoint.point()).isLessThanOrEqualTo(UserPoint.MAX_POINT);
        }
        static Stream<Arguments> UserPoint_have_0_input_data() {
            return Stream.of(
                    Arguments.of(1L, 1_000_000L),
                    Arguments.of(1L, 999_999L)
            );
        }
        @ParameterizedTest
        @MethodSource("UserPoint_have1000_input_data")
        public void 포인트_보유된_상태에서_포인트_충전_In_Range_성공_케이스(long id, long amount) throws Exception {
            // Given
            UserPoint initUser = new UserPoint(id, 1_000L, 0L);
            // When
            var chargedPoint = initUser.charge(amount);
            // Then
            assertThat(chargedPoint.point()).isLessThanOrEqualTo(UserPoint.MAX_POINT);
        }
        static Stream<Arguments> UserPoint_have1000_input_data() {
            return Stream.of(
                    Arguments.of(1L, 999_000L),
                    Arguments.of(1L, 998_999L)
            );
        }
        @Test
        public void 포인트_충전_Out_of_Range_실패_케이스() throws Exception {
            // Given
            long inputId = 1L;
            UserPoint initUser = UserPoint.empty(inputId);
            long inputPoint = 1_000_001L;
            // When && Then
            assertThatExceptionOfType(ExceedMaxPointBalanceException.class)
                    .isThrownBy(() -> initUser.charge(inputPoint))
                    .withMessage(String.format(ExceedMaxPointBalanceException.ERROR_MESSAGE, inputPoint, inputPoint - UserPoint.MAX_POINT));
        }
        @Test
        public void 포인트_충전_최소금액_정책에_따른_실패_케이스() throws Exception {
            // Given
            long inputId = 1L;
            UserPoint initUser = UserPoint.empty(inputId);
            long inputPoint = 0L;
            // When && Then
            assertThatExceptionOfType(InvalidChargeAmountException.class)
                    .isThrownBy(() -> initUser.charge(inputPoint))
                    .withMessage(InvalidChargeAmountException.ERROR_MESSAGE);
        }
    }
}
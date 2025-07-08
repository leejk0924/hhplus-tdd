package io.hhplus.tdd.point;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserPointTest {

//    # 정책
//    - 포인트는 최대 100만원, 최소 0원까지 포인트를 보유할 수 있다.
//    - 포인트 0원 이하 충전 시도 시 실패 처리 (IlligalArgumentException)
//    - 포인트 충전 시 히스토리 기록

//      1. 포인트를 충전 시 보유 포인트가 99_999원 일 경우 성공 케이스
//      2. 포인트를 충전 시 보유 포인트가 100_000원 일 경우 성공 케이스
//      3. 포인트를 충전 시 보유 포인트가 100_001원 일 경우 실패 케이스 : ExceededPointException / "~원 을 더 하면 최대 포인트 값 ~원을 초과합니다."
//      4. 포인트 0원 이하의 금액을 충전했을 때 실패 케이스 : IlligalArgumentException / "충전 금액은 1원 이상이어야 합니다."
//      5. amount 파라미터가 null 이거나, 문자형태 일 경우 테스트 : BadRequest / "amount 는 필수 이며 숫자여야 합니다."
@Nested
@DisplayName("포인트 충전 테스트")
    class chargeTest{
        @Test
        public void 포인트_충전_In_Range_성공_케이스() throws Exception {
            // Given
            long initUserId = 1L;
            UserPoint initUser = UserPoint.empty(initUserId);
            long input = 99_999L;
            var chargedPoint = initUser.charge(input);
            // When

            // Then
            assertThat(chargedPoint.point()).isEqualTo(input);
        }
    }
}
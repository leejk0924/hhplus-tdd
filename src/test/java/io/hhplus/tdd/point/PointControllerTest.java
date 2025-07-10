package io.hhplus.tdd.point;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.time.TestTimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class PointControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserPointTable userPointTable;
    private UserPointService userPointService;

    @Nested
    @DisplayName("포인트 충전 테스트")
    class chargeTest {
        @BeforeEach
        void setUp() {
            userPointService = new UserPointService(
                    userPointTable,
                    new PointHistoryTable(),
                    new TestTimeProvider(0L));

            Map<Long, Long> fakeDb = new HashMap<>();

            given(userPointTable.selectById(anyLong())).willAnswer(
                    i -> {
                        long id = i.getArgument(0);
                        Long point = fakeDb.getOrDefault(id, 0L);
                        return new UserPoint(id, point, 0L);
                    });

            given(userPointTable.insertOrUpdate(anyLong(), anyLong()))
                    .willAnswer(
                            i -> {
                                long inputId = i.getArgument(0);
                                long inputAmount = i.getArgument(1);
                                fakeDb.put(inputId, inputAmount);
                                return new UserPoint(inputId, inputAmount, 0L);
                            }
                    );
        }

        @Test
        public void 유저의_포인트_충전_성공_테스트() throws Exception {
            // Given
            long initId = 1L;

            // When && Then
            mockMvc.perform(
                            patch("/point/{id}/charge", initId)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .content("2000")
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.point").value(2000));
        }

        @Test
        void 포인트_충전_최대한도_초과_실패_테스트() throws Exception {
            // Given
            long initId = 1L;
            userPointTable.insertOrUpdate(initId, 1_000_000L);

            // When && Then
            mockMvc.perform(patch("/point/{id}/charge", initId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("1")
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("최대 보유 포인트 금액을 초과하였습니다. 올바른 충전 금액을 입력해 주세요."));
        }
    }

    @Nested
    @DisplayName("포인트 조회 테스트")
    class searchPoint {
        @BeforeEach
        void setUp() {
            userPointService = new UserPointService(
                    userPointTable,
                    new PointHistoryTable(),
                    new TestTimeProvider(0L));

            Map<Long, Long> fakeDb = new HashMap<>();

            given(userPointTable.selectById(anyLong())).willAnswer(
                    i -> {
                        long id = i.getArgument(0);
                        Long point = fakeDb.getOrDefault(id, 0L);
                        return new UserPoint(id, point, 0L);
                    });

            given(userPointTable.insertOrUpdate(anyLong(), anyLong()))
                    .willAnswer(
                            i -> {
                                long inputId = i.getArgument(0);
                                long inputAmount = i.getArgument(1);
                                fakeDb.put(inputId, inputAmount);
                                return new UserPoint(inputId, inputAmount, 0L);
                            }
                    );
        }

        @Test
        public void 초기유저_포인트_조회_테스트() throws Exception {
            // Given
            long initId = 1L;

            // When && Then
            mockMvc.perform(get("/point/{id}", initId)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.point").value(0));
        }

        @Test
        public void 유저의_포인트를_1회_충전후_포인트_조회_테스트() throws Exception {
            // Given
            long initId = 1L;
            long amount = 1_000_000L;
            UserPoint initUserPoint = new UserPoint(initId, 0L, 0L);
            // When && Then
            userPointService.chargePoint(initId, amount);
            mockMvc.perform(get("/point/{id}", initId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.point").value(amount));
        }
    }
    @Nested
    class useTest {
        @BeforeEach
        void setUp() {
            userPointService = new UserPointService(
                    userPointTable,
                    new PointHistoryTable(),
                    new TestTimeProvider(0L));

            Map<Long, Long> fakeDb = new HashMap<>();

            given(userPointTable.selectById(anyLong())).willAnswer(
                    i -> {
                        long id = i.getArgument(0);
                        Long point = fakeDb.getOrDefault(id, 0L);
                        return new UserPoint(id, point, 0L);
                    });

            given(userPointTable.insertOrUpdate(anyLong(), anyLong()))
                    .willAnswer(
                            i -> {
                                long inputId = i.getArgument(0);
                                long inputAmount = i.getArgument(1);
                                fakeDb.put(inputId, inputAmount);
                                return new UserPoint(inputId, inputAmount, 0L);
                            }
                    );
        }
        @Test
        public void 포인트_사용_성공_테스트() throws Exception {
            // Given
            long initId = 1L;
            long amount = 3000L;
            long initPoint = 500_000;
            userPointTable.insertOrUpdate(1L, initPoint);
            String body = objectMapper.writeValueAsString(amount);

            // When && Then
            mockMvc.perform(
                            patch("/point/{id}/use", initId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.point").value(initPoint - amount));
        }
        @ParameterizedTest
        @MethodSource("UserPointController_Use_Fail_Test")
        public void 포인트_사용_실패_테스트 (long initId, long initPoint, long amount, String errorMessage) throws Exception {
            // Given
            String body = objectMapper.writeValueAsString(amount);
            userPointTable.insertOrUpdate(1L, initPoint);

            // When && Then
            mockMvc.perform(
                            patch("/point/{id}/use", initId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value(errorMessage));
        }
        static Stream<Arguments> UserPointController_Use_Fail_Test() {
            return Stream.of(
                    // 사용 가능 금액 정책을 위반 하였을 경우
                    Arguments.of(1L, 1_000_000L, 1_000_001L, "사용가능한 금액은 1원 이상, 1000000원 이하입니다. 사용하실 포인트 금액을 다시 입력해 주세요"),
                    // 보유 금액보다 많은 금액 사용하였을 경우
                    Arguments.of(1L, 0L, 1L, "잔액이 부족합니다. 포인트를 충전해주세요.")
            );
        }
    }
}
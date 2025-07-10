package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.time.TestTimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

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
    @MockBean
    private UserPointTable userPointTable;
    private UserPointService userPointService;

    @Nested
    @DisplayName("포인트 충전 테스트")
    class chargeTest{
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
    }
}
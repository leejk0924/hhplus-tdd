package io.hhplus.tdd;

import io.hhplus.tdd.error.ExceedMaxPointBalanceException;
import io.hhplus.tdd.error.InvalidChargeAmountException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
class ApiControllerAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = ExceedMaxPointBalanceException.class)
    public ResponseEntity<ErrorResponse> exceedMaxPointHandleException() {
        return ResponseEntity.status(400).body(new ErrorResponse("400", "최대 보유 포인트 금액을 초과하였습니다. 올바른 충전 금액을 입력해 주세요."));
    }

    @ExceptionHandler(value = InvalidChargeAmountException.class)
    public ResponseEntity<ErrorResponse> invalidChargeAmountHandlerException() {
        return ResponseEntity.status(400).body(new ErrorResponse("400", "최소 충전 금액이 1원 이상입니다. 올바른 충전 금액을 입력해 주세요."));
    }
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity.status(500).body(new ErrorResponse("500", "에러가 발생했습니다."));
    }
}

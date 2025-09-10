package com.snapreserve.snapreserve.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalHttpExceptionHandler {

    @ExceptionHandler(NoAvailableSlotsException.class)
    public ErrorResponse handleBusinessException(NoAvailableSlotsException ex) {
        return ErrorResponse.create(ex, HttpStatusCode.valueOf(409), ex.getMessage());
    }

}

package com.ahold.ctp.sandbox.infrastructure.http.controllers.advices

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.server.ResponseStatusException

@ControllerAdvice
class DeliveryControllerExceptionHandler {
    @ExceptionHandler(value = [Throwable::class])
    fun handleException(throwable: Throwable): ResponseStatusException {
        if (throwable is IllegalArgumentException) {
            return ResponseStatusException(HttpStatus.BAD_REQUEST, throwable.message)
        } else return ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, throwable.message)
    }
}
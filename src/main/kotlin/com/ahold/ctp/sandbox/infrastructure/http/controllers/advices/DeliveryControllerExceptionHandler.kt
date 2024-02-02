package com.ahold.ctp.sandbox.infrastructure.http.controllers.advices

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.server.ResponseStatusException

@ControllerAdvice
class DeliveryControllerExceptionHandler {
    @ExceptionHandler(value = [Throwable::class])
    fun handleException(throwable: Throwable) {
        if (throwable is IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, throwable.message)
        } else throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, throwable.message)
    }
}
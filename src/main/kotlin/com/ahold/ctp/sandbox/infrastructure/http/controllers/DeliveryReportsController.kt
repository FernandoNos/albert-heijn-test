package com.ahold.ctp.sandbox.infrastructure.http.controllers

import com.ahold.ctp.sandbox.domain.ports.DeliveryReportsServiceInterface
import com.ahold.ctp.sandbox.domain.valueobjects.AverageMinutesBetweenDeliveryReport
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("deliveries")
class DeliveryReportsController(
    private val deliveryService: DeliveryReportsServiceInterface
) {
    @GetMapping("business-summary")
    fun getDeliveries(): ResponseEntity<AverageMinutesBetweenDeliveryReport> {
        val result = deliveryService.getYesterdayDeliveriesReportByDate()
        return ResponseEntity.ok(result)
    }
}
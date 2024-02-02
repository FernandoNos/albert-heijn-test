package com.ahold.ctp.sandbox.domain.valueobjects

data class AverageMinutesBetweenDeliveryReport(
    val deliveries: Int,
    val averageMinutesBetweenDeliveryStart: Int
)
package com.ahold.ctp.sandbox.domain.ports

import com.ahold.ctp.sandbox.domain.valueobjects.AverageMinutesBetweenDeliveryReport

interface DeliveryReportsServiceInterface {
    fun getYesterdayDeliveriesReportByDate(): AverageMinutesBetweenDeliveryReport
}
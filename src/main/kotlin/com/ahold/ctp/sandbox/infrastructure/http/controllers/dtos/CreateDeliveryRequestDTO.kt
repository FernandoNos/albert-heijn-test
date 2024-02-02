package com.ahold.ctp.sandbox.infrastructure.http.controllers.dtos

import com.ahold.ctp.sandbox.domain.enums.DeliveryStatus
import com.ahold.ctp.sandbox.domain.valueobjects.CreateDelivery
import com.ahold.ctp.sandbox.domain.valueobjects.VehicleId
import java.time.ZonedDateTime


data class CreateDeliveryRequestDTO(
    val vehicleId: String,
    val startedAt: ZonedDateTime,
    val status: DeliveryStatus
)

fun CreateDeliveryRequestDTO.toCreateDelivery() =
    CreateDelivery(
        VehicleId(this.vehicleId),
        this.startedAt,
        this.status
    )
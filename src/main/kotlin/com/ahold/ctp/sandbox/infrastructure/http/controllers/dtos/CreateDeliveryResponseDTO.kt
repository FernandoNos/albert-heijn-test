package com.ahold.ctp.sandbox.infrastructure.http.controllers.dtos

import com.ahold.ctp.sandbox.domain.enums.DeliveryStatus
import com.ahold.ctp.sandbox.domain.valueobjects.DeliveryObject
import java.time.ZonedDateTime
import java.util.*

data class CreateDeliveryResponseDTO(
    val id: UUID,
    val vehicleId: String,
    val startedAt: ZonedDateTime,
    val finishedAt: ZonedDateTime? = null,
    val status: DeliveryStatus
)

fun DeliveryObject.toCreateDeliveryResponseDTO() =
    CreateDeliveryResponseDTO(
        this.id,
        this.vehicleId.id,
        this.startedAt,
        this.finishedAt,
        this.status
    )
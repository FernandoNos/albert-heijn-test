package com.ahold.ctp.sandbox.infrastructure.http.controllers.dtos

import com.ahold.ctp.sandbox.domain.enums.DeliveryStatus
import com.ahold.ctp.sandbox.domain.valueobjects.DeliveryObject
import com.ahold.ctp.sandbox.domain.valueobjects.UpdateDelivery
import org.hibernate.sql.Update
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.time.ZonedDateTime
import java.util.*

data class UpdateDeliveryRequestDTO(
    val id: UUID? = null,
    val finishedAt: ZonedDateTime? = null,
    val status: DeliveryStatus
)

fun UpdateDeliveryRequestDTO.toUpdateDelivery(id: UUID?): UpdateDelivery {
    if (id == null) {
        throw ResponseStatusException(HttpStatus.BAD_REQUEST, "the field id cannot be null")
    }
    return UpdateDelivery(
        id,
        this.finishedAt,
        this.status
    )
}

data class UpdateDeliveryResponseDTO(
    val id: UUID,
    val vehicleId: String,
    val startedAt: ZonedDateTime,
    val finishedAt: ZonedDateTime? = null,
    val status: DeliveryStatus
)

data class UpdateDeliveriesResponseDTO(
    val deliveries: List<UpdateDeliveryResponseDTO>
)

fun DeliveryObject.toUpdateDeliveryResponseDTO() =
    UpdateDeliveryResponseDTO(
        this.id,
        this.vehicleId.id,
        this.startedAt,
        this.finishedAt,
        this.status
    )
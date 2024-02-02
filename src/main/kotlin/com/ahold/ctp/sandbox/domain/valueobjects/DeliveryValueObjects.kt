package com.ahold.ctp.sandbox.domain.valueobjects

import com.ahold.ctp.sandbox.domain.enums.DeliveryStatus
import com.ahold.ctp.sandbox.repositories.entities.DeliveryEntity
import com.ahold.ctp.sandbox.repositories.entities.VehicleEntity
import com.ahold.ctp.sandbox.utils.toTimestamp
import com.ahold.ctp.sandbox.utils.toZonedDateTimeUTC
import java.time.ZonedDateTime
import java.util.*

data class DeliveryObject(
    val id: UUID,
    val status: DeliveryStatus,
    val startedAt: ZonedDateTime,
    val finishedAt: ZonedDateTime? = null,
    val vehicleId: VehicleId
)

data class UpdateDelivery(
    val id: UUID,
    val finishedAt: ZonedDateTime? = null,
    val status: DeliveryStatus
) {
    init {
        finishedAt?.let {
            if (status != DeliveryStatus.DELIVERED) {
                throw IllegalArgumentException("Only DELIVERED can be set with finishedAt - Delivery ID $id")
            }
        } ?: run {
            if (status == DeliveryStatus.DELIVERED) {
                throw IllegalArgumentException("finishedAt must be provided when status is DELIVERED - Delivery ID $id")
            }
        }

    }
}

data class CreateDelivery(
    val vehicleId: VehicleId,
    val startedAt: ZonedDateTime,
    val status: DeliveryStatus
) {
    init {
        if (status == DeliveryStatus.DELIVERED) {
            throw IllegalArgumentException("DELIVERED cannot be used while creating a new delivery")
        }
        if (startedAt.toTimestamp().time > ZonedDateTime.now().toTimestamp().time) {
            throw IllegalArgumentException("startedAt cannot be in the future!")
        }
    }
}

data class VehicleId(val id: String) {
    init {
        val regexPattern = Regex("\\w\\w\\w-\\d\\d\\d")
        if (!id.matches(regexPattern))
            throw IllegalArgumentException("Invalid Vehicle id received - $id")
    }
}

fun CreateDelivery.toDeliveryEntity(vehicle: VehicleEntity) =
    DeliveryEntity(
        status = this.status,
        startedAt = this.startedAt.toTimestamp(),
        deliveredBy = vehicle
    )

fun DeliveryEntity.toDeliveryObject(): DeliveryObject {
    if (this.id == null)
        throw IllegalStateException("DeliveryEntity.id cannot be null")

    return DeliveryObject(
        this.id,
        this.status,
        this.startedAt.toZonedDateTimeUTC(),
        this.finishedAt?.toZonedDateTimeUTC(),
        VehicleId(this.deliveredBy.vehicleId),
    )
}
package com.ahold.ctp.sandbox.domain

import com.ahold.ctp.sandbox.domain.ports.DeliveryServiceInterface
import com.ahold.ctp.sandbox.domain.ports.VehicleServiceInterface
import com.ahold.ctp.sandbox.domain.valueobjects.*
import com.ahold.ctp.sandbox.repositories.DeliveryRepository
import com.ahold.ctp.sandbox.repositories.entities.DeliveryEntity
import com.ahold.ctp.sandbox.utils.toTimestamp
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class DeliveryServiceImpl(
    private val deliveryRepository: DeliveryRepository,
    private val vehicleService: VehicleServiceInterface
) : DeliveryServiceInterface {
    override fun createDelivery(createDelivery: CreateDelivery): DeliveryObject {
        val vehicle = vehicleService.findOrCreateVehicleByPlate(createDelivery.vehicleId.id)
        val newDelivery = createDelivery.toDeliveryEntity(vehicle)
        return deliveryRepository.save(newDelivery).toDeliveryObject()
    }

    override fun updateDelivery(updateDelivery: UpdateDelivery): DeliveryObject {
        val optionalDelivery = deliveryRepository.findById(updateDelivery.id)

        val delivery = validateAndGetDelivery(optionalDelivery, updateDelivery)

        delivery.finishedAt = updateDelivery.finishedAt?.let { updateDelivery.finishedAt.toTimestamp() }
        delivery.status = updateDelivery.status

        return deliveryRepository.save(delivery).toDeliveryObject()
    }

    @Transactional
    override fun updateDeliveries(updateDelivery: List<UpdateDelivery>): List<DeliveryObject> =
        updateDelivery.map {
            updateDelivery(it)
        }

    private fun validateAndGetDelivery(
        optionalDelivery: Optional<DeliveryEntity>,
        updateDelivery: UpdateDelivery
    ): DeliveryEntity {

        if (optionalDelivery.isEmpty) {
            throw IllegalArgumentException("Delivery ${updateDelivery.id} not found!")
        }
        val existingDelivery = optionalDelivery.get()

        updateDelivery.finishedAt?.let {
            if (existingDelivery.startedAt.time == updateDelivery.finishedAt.toTimestamp().time) {
                throw IllegalArgumentException("startedAt must not be equal to finishedAt - Delivery ID: ${updateDelivery.id}")
            }
            if (existingDelivery.startedAt.after(updateDelivery.finishedAt.toTimestamp())) {
                throw IllegalArgumentException("startedAt must be greater than finishedAt - Delivery ID: ${updateDelivery.id}")
            }
        }

        return existingDelivery
    }

}



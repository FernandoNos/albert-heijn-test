package com.ahold.ctp.sandbox.domain.ports

import com.ahold.ctp.sandbox.domain.valueobjects.CreateDelivery
import com.ahold.ctp.sandbox.domain.valueobjects.DeliveryObject
import com.ahold.ctp.sandbox.domain.valueobjects.UpdateDelivery

interface DeliveryServiceInterface {
    fun createDelivery(createDelivery: CreateDelivery): DeliveryObject
    fun updateDelivery(updateDelivery: UpdateDelivery): DeliveryObject
    fun updateDeliveries(updateDelivery: List<UpdateDelivery>): List<DeliveryObject>
}
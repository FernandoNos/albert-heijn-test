package com.ahold.ctp.sandbox.infrastructure.http.controllers

import com.ahold.ctp.sandbox.domain.ports.DeliveryServiceInterface
import com.ahold.ctp.sandbox.domain.valueobjects.toDeliveryObject
import com.ahold.ctp.sandbox.infrastructure.http.controllers.dtos.*
import com.ahold.ctp.sandbox.repositories.DeliveryRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("deliveries")
class DeliveryController(
    private val deliveryService: DeliveryServiceInterface
) {
    @PostMapping
    fun createDelivery(
        @RequestBody
        createDeliveryRequestDTO: CreateDeliveryRequestDTO
    ): ResponseEntity<CreateDeliveryResponseDTO> {
        val result = deliveryService.createDelivery(createDeliveryRequestDTO.toCreateDelivery())
        return ResponseEntity.ok(result.toCreateDeliveryResponseDTO())
    }

    @PatchMapping("/{id}")
    fun updateDelivery(
        @PathVariable id: UUID,
        @RequestBody updateDeliveryRequestDTO: UpdateDeliveryRequestDTO
    ): ResponseEntity<UpdateDeliveryResponseDTO> {
        val result = deliveryService.updateDelivery(updateDeliveryRequestDTO.toUpdateDelivery(id))
        return ResponseEntity.ok(result.toUpdateDeliveryResponseDTO())
    }

    @PatchMapping("bulk-update")
    fun bulkUpdateDeliveries(
        @RequestBody bulkUpdate: List<UpdateDeliveryRequestDTO>
    ): ResponseEntity<UpdateDeliveriesResponseDTO> {
        val updateDeliveries = bulkUpdate.map { it.toUpdateDelivery(it.id) }
        val result = deliveryService.updateDeliveries(updateDeliveries)
            .map { it.toUpdateDeliveryResponseDTO() }

        return ResponseEntity.ok(UpdateDeliveriesResponseDTO(result))
    }
}
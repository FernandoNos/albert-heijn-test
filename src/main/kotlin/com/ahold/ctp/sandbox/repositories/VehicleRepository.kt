package com.ahold.ctp.sandbox.repositories

import com.ahold.ctp.sandbox.repositories.entities.VehicleEntity
import org.springframework.data.repository.CrudRepository
import java.util.*

interface VehicleRepository : CrudRepository<VehicleEntity, UUID> {
    fun findByVehicleId(place: String): VehicleEntity?
}
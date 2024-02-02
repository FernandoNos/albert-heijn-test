package com.ahold.ctp.sandbox.domain.ports

import com.ahold.ctp.sandbox.repositories.entities.VehicleEntity

interface VehicleServiceInterface {
    fun findOrCreateVehicleByPlate(vehicleId: String): VehicleEntity
}
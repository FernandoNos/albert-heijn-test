package com.ahold.ctp.sandbox.domain

import com.ahold.ctp.sandbox.domain.ports.VehicleServiceInterface
import com.ahold.ctp.sandbox.repositories.VehicleRepository
import com.ahold.ctp.sandbox.repositories.entities.VehicleEntity
import org.springframework.stereotype.Service

@Service
class VehicleServiceImpl(
    val vehicleRepository: VehicleRepository
) : VehicleServiceInterface {
    override fun findOrCreateVehicleByPlate(vehicleId: String): VehicleEntity {
        var vehicle = vehicleRepository.findByVehicleId(vehicleId)
        if (vehicle == null) {
            vehicle = VehicleEntity()
            vehicle.vehicleId = vehicleId
            return vehicleRepository.save(vehicle)
        }
        return vehicle
    }
}
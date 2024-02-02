package com.ahold.ctp.sandbox.domain

import com.ahold.ctp.sandbox.domain.ports.VehicleServiceInterface
import com.ahold.ctp.sandbox.repositories.VehicleRepository
import com.ahold.ctp.sandbox.repositories.entities.VehicleEntity
import org.springframework.stereotype.Service

@Service
class VehicleServiceImpl(
    val vehicleRepository: VehicleRepository
) : VehicleServiceInterface {
    override fun findOrCreateVehicleByPlate(plate: String): VehicleEntity {
        var vehicle = vehicleRepository.findByVehicleId(plate)
        if (vehicle == null) {
            vehicle = VehicleEntity()
            vehicle.vehicleId = plate
            return vehicleRepository.save(vehicle)
        }
        return vehicle
    }
}
package com.ahold.ctp.sandbox.repositories.entities

import jakarta.persistence.*
import java.util.*

@Entity(name = "vehicle")
class VehicleEntity(
    @Id
    @GeneratedValue
    @Column
    val id: UUID,
    @Column(name = "vehicle_id", nullable = false, unique = true)
    var vehicleId: String,

    @OneToMany(mappedBy = "deliveredBy")
    val delivers: MutableList<DeliveryEntity> = arrayListOf()
) {
    constructor() : this(
        UUID.randomUUID(),
        ""
    )
}
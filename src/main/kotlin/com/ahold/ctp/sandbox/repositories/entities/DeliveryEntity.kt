package com.ahold.ctp.sandbox.repositories.entities

import com.ahold.ctp.sandbox.domain.enums.DeliveryStatus
import jakarta.persistence.*
import java.sql.Timestamp
import java.util.*

@Entity(name = "delivery")
class DeliveryEntity(
    @Id
    @GeneratedValue
    @Column
    val id: UUID? = null,
    @Column(nullable = false)
    var status: DeliveryStatus,
    @Column(name = "started_at", nullable = false)
    val startedAt: Timestamp,
    @Column(name = "finished_at")
    var finishedAt: Timestamp? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="delivered_by", nullable=false)
    val deliveredBy: VehicleEntity
)
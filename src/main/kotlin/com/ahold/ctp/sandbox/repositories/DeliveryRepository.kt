package com.ahold.ctp.sandbox.repositories

import com.ahold.ctp.sandbox.repositories.entities.DeliveryEntity
import org.springframework.data.repository.CrudRepository
import java.sql.Timestamp
import java.util.*

interface DeliveryRepository : CrudRepository<DeliveryEntity, UUID> {
    fun findAllByStartedAtBetweenOrderByStartedAt(startTime: Timestamp, endTime: Timestamp): List<DeliveryEntity>
}
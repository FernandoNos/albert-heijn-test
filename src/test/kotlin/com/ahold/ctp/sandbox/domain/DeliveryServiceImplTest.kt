package com.ahold.ctp.sandbox.domain

import com.ahold.ctp.sandbox.domain.enums.DeliveryStatus
import com.ahold.ctp.sandbox.domain.valueobjects.CreateDelivery
import com.ahold.ctp.sandbox.domain.valueobjects.DeliveryObject
import com.ahold.ctp.sandbox.domain.valueobjects.UpdateDelivery
import com.ahold.ctp.sandbox.domain.valueobjects.VehicleId
import com.ahold.ctp.sandbox.repositories.DeliveryRepository
import com.ahold.ctp.sandbox.repositories.entities.DeliveryEntity
import com.ahold.ctp.sandbox.utils.toTimestamp
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.ZonedDateTime
import java.util.*

private const val vehicleId = "VEH-123"

@SpringBootTest
@ExtendWith(MockitoExtension::class)
class DeliveryServiceImplTest {

    @Autowired
    private lateinit var deliveryServiceImpl: DeliveryServiceImpl

    @Autowired
    private lateinit var deliveryRepository: DeliveryRepository

    @AfterEach
    fun cleanUp() {
        deliveryRepository.deleteAll()
    }

    @Test
    fun create_delivery_success() {
        val newDelivery = createDeliveryObject()

        val optionalDelivery = deliveryRepository.findById(newDelivery.id)
        assert(optionalDelivery.isPresent)

        val dbDelivery = optionalDelivery.get()
        assert(newDelivery.vehicleId.id == dbDelivery.deliveredBy.vehicleId)
        assert(dbDelivery.status == DeliveryStatus.IN_PROGRESS)
        assert(dbDelivery.finishedAt == null)
        assert(dbDelivery.startedAt.time == newDelivery.startedAt.toTimestamp().time)
    }

    @Test
    fun create_delivery_does_not_accept_DELIVERED_status() {
        val exception = assertThrows<IllegalArgumentException> {
            createDeliveryObject(status = DeliveryStatus.DELIVERED)
        }
        assert(exception.message == "DELIVERED cannot be used while creating a new delivery")
    }

    @ParameterizedTest
    @MethodSource("getDatesInTheFuture")
    fun create_delivery_does_not_accept_startedAt_in_the_future(dateTime: ZonedDateTime) {
        val exception = assertThrows<IllegalArgumentException> {
            createDeliveryObject(startedAt = dateTime)
        }
        assert(exception.message == "startedAt cannot be in the future!")
    }

    @Test
    fun create_delivery_does_not_accept_INVALID_vehicle_id() {
        val exception = assertThrows<IllegalArgumentException> {
            createDeliveryObject(vehicleId = "ABC")
        }
        assert(exception.message == "Invalid Vehicle id received - ABC")
    }

    @Test
    fun update_delivery_DELIVERED_success() {
        val newDelivery = createDeliveryObject()
        val updateDelivery = UpdateDelivery(
            id = newDelivery.id,
            finishedAt = ZonedDateTime.now(),
            DeliveryStatus.DELIVERED
        )

        val result = deliveryServiceImpl.updateDelivery(updateDelivery)
        val optionalDbDelivery = deliveryRepository.findById(newDelivery.id)
        assert(optionalDbDelivery.isPresent)

        val dbDelivery = optionalDbDelivery.get()
        assertDeliveries(result, dbDelivery)
    }

    @Test
    fun update_delivery_DELIVERED_error_when_finishedAt_before_startedAt() {
        val newDelivery = createDeliveryObject()
        val updateDelivery = UpdateDelivery(
            id = newDelivery.id,
            finishedAt = newDelivery.startedAt.minusDays(1),
            DeliveryStatus.DELIVERED
        )

        val exception = assertThrows<IllegalArgumentException> {
            deliveryServiceImpl.updateDelivery(updateDelivery)
        }
        assert(exception.message == "startedAt must be greater than finishedAt - Delivery ID: ${updateDelivery.id}")

        val optionalDbDelivery = deliveryRepository.findById(newDelivery.id)
        assert(optionalDbDelivery.isPresent)

        val dbDelivery = optionalDbDelivery.get()
        assertDeliveries(newDelivery, dbDelivery)
    }

    @Test
    fun update_delivery_DELIVERED_error_when_finishedAt_equals_startedAt() {
        val newDelivery = createDeliveryObject()
        val updateDelivery = UpdateDelivery(
            id = newDelivery.id,
            finishedAt = newDelivery.startedAt,
            DeliveryStatus.DELIVERED
        )

        val exception = assertThrows<IllegalArgumentException> {
            deliveryServiceImpl.updateDelivery(updateDelivery)
        }
        assert(exception.message == "startedAt must not be equal to finishedAt - Delivery ID: ${updateDelivery.id}")

        val optionalDbDelivery = deliveryRepository.findById(newDelivery.id)
        assert(optionalDbDelivery.isPresent)

        val dbDelivery = optionalDbDelivery.get()
        assertDeliveries(newDelivery, dbDelivery)
    }

    @Test
    fun update_delivery_error_delivery_not_found() {
        val randomId = UUID.randomUUID()
        val updateDelivery = UpdateDelivery(
            id = randomId,
            status = DeliveryStatus.IN_PROGRESS
        )

        val exception = assertThrows<IllegalArgumentException> {
            deliveryServiceImpl.updateDelivery(updateDelivery)
        }
        assert(exception.message == "Delivery $randomId not found!")
    }

    @Test
    fun update_deliveries_success() {
        val firstNewDelivery = createDeliveryObject(vehicleId = "FIR-123")
        val secondNewDelivery = createDeliveryObject(vehicleId = "SEC-123")
        val firstUpdateDelivery = UpdateDelivery(
            id = firstNewDelivery.id,
            finishedAt = ZonedDateTime.now(),
            DeliveryStatus.DELIVERED
        )

        val secondUpdateDelivery = UpdateDelivery(
            id = secondNewDelivery.id,
            finishedAt = ZonedDateTime.now(),
            DeliveryStatus.DELIVERED
        )

        val result = deliveryServiceImpl.updateDeliveries(listOf(firstUpdateDelivery, secondUpdateDelivery))
        assert(result.size == 2)

        val firstOptionalDbDelivery = deliveryRepository.findById(firstNewDelivery.id)
        assert(firstOptionalDbDelivery.isPresent)
        val secondOptionalDbDelivery = deliveryRepository.findById(secondNewDelivery.id)
        assert(secondOptionalDbDelivery.isPresent)

        val firstDbDelivery = firstOptionalDbDelivery.get()
        val secondDbDelivery = secondOptionalDbDelivery.get()
        assertDeliveries(result[0], firstDbDelivery)
        assertDeliveries(result[1], secondDbDelivery)
    }

    @Test
    fun update_deliveries_error_transaction() {
        val firstNewDelivery = createDeliveryObject(vehicleId = "FIR-123")
        val secondNewDelivery = createDeliveryObject(vehicleId = "SEC-123")
        val firstUpdateDelivery = UpdateDelivery(
            id = firstNewDelivery.id,
            finishedAt = ZonedDateTime.now(),
            DeliveryStatus.DELIVERED
        )

        val secondUpdateDelivery = mock(UpdateDelivery::class.java)

        assertThrows<RuntimeException> {
            deliveryServiceImpl.updateDeliveries(listOf(firstUpdateDelivery, secondUpdateDelivery))
        }

        val firstOptionalDbDelivery = deliveryRepository.findById(firstNewDelivery.id)
        assert(firstOptionalDbDelivery.isPresent)
        val secondOptionalDbDelivery = deliveryRepository.findById(secondNewDelivery.id)
        assert(secondOptionalDbDelivery.isPresent)

        val firstDbDelivery = firstOptionalDbDelivery.get()
        val secondDbDelivery = secondOptionalDbDelivery.get()
        assertDeliveries(firstNewDelivery, firstDbDelivery)
        assertDeliveries(secondNewDelivery, secondDbDelivery)
    }

    private fun createDeliveryObject(
        vehicleId: String = com.ahold.ctp.sandbox.domain.vehicleId,
        startedAt: ZonedDateTime = ZonedDateTime.now(),
        status: DeliveryStatus = DeliveryStatus.IN_PROGRESS
    ) = deliveryServiceImpl.createDelivery(
        CreateDelivery(
            VehicleId(vehicleId),
            startedAt,
            status
        )
    )

    private fun assertDeliveries(
        result: DeliveryObject,
        dbDelivery: DeliveryEntity
    ) {
        assert(result.id == dbDelivery.id)
        assert(result.status == dbDelivery.status)
        assert(result.vehicleId.id == dbDelivery.deliveredBy.vehicleId)
        assert(result.startedAt.toTimestamp().time == dbDelivery.startedAt.time)
        assert(result.finishedAt?.toTimestamp()?.time == dbDelivery.finishedAt?.time)
    }

    companion object {
        @JvmStatic
        fun getDatesInTheFuture(): List<ZonedDateTime> = listOf(
            ZonedDateTime.now().plusDays(1),
            ZonedDateTime.now().plusHours(1),
            ZonedDateTime.now().plusMinutes(1),
            ZonedDateTime.now().plusSeconds(5),
            ZonedDateTime.now().plusMonths(1),
            ZonedDateTime.now().plusYears(1)
        )
    }
}
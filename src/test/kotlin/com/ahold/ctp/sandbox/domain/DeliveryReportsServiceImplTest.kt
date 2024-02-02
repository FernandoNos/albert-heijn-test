package com.ahold.ctp.sandbox.domain

import com.ahold.ctp.sandbox.domain.enums.DeliveryStatus
import com.ahold.ctp.sandbox.domain.ports.DeliveryReportsServiceInterface
import com.ahold.ctp.sandbox.domain.ports.DeliveryServiceInterface
import com.ahold.ctp.sandbox.domain.valueobjects.CreateDelivery
import com.ahold.ctp.sandbox.domain.valueobjects.VehicleId
import com.ahold.ctp.sandbox.repositories.DeliveryRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import java.time.*

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DeliveryReportsServiceImplTest {
    @Autowired
    private lateinit var deliveryServiceImpl: DeliveryServiceInterface

    @Autowired
    private lateinit var deliveryReportServiceImpl: DeliveryReportsServiceInterface

    @Autowired
    private lateinit var deliveryRepository: DeliveryRepository

    @BeforeEach
    fun cleanUp() {
        deliveryRepository.deleteAll()
    }

    @Test
    fun report_when_no_entry() {
        val result = deliveryReportServiceImpl.getYesterdayDeliveriesReportByDate()
        assert(result.deliveries == 0)
        assert(result.averageMinutesBetweenDeliveryStart == 0)
    }

    @Test
    fun report_when_one_entry() {
        createDeliveryObject(
            startedAt = createDate()
        )
        val result = deliveryReportServiceImpl.getYesterdayDeliveriesReportByDate()
        assert(result.deliveries == 1)
        assert(result.averageMinutesBetweenDeliveryStart == 0)
    }

    @Test
    fun report_scenario_1() {
        createDeliveryObject(
            startedAt = createDate(hour = 1)
        )
        createDeliveryObject(
            startedAt = createDate(hour = 3)
        )
        createDeliveryObject(
            startedAt = createDate(hour = 9)
        )
        val result = deliveryReportServiceImpl.getYesterdayDeliveriesReportByDate()
        assert(result.deliveries == 3)
        assert(result.averageMinutesBetweenDeliveryStart == 240)
    }

    @Test
    fun report_scenario_all_started_dates_are_equal() {
        createDeliveryObject(startedAt = createDate())
        createDeliveryObject(startedAt = createDate())
        createDeliveryObject(startedAt = createDate())

        val result = deliveryReportServiceImpl.getYesterdayDeliveriesReportByDate()
        assert(result.deliveries == 3)
        assert(result.averageMinutesBetweenDeliveryStart == 0)
    }

    @Test
    fun report_scenario_created_today() {
        createDeliveryObject(startedAt = ZonedDateTime.now())
        val result = deliveryReportServiceImpl.getYesterdayDeliveriesReportByDate()
        assert(result.deliveries == 0)
        assert(result.averageMinutesBetweenDeliveryStart == 0)
    }

    @Test
    fun report_scenario_created_two_days_ago() {
        createDeliveryObject(startedAt = createDate(minusDays = 2))
        val result = deliveryReportServiceImpl.getYesterdayDeliveriesReportByDate()
        assert(result.deliveries == 0)
        assert(result.averageMinutesBetweenDeliveryStart == 0)
    }

    @Test
    fun report_scenario_created_minutes_apart() {
        createDeliveryObject(startedAt = createDate(minute = 0))
        createDeliveryObject(startedAt = createDate(minute = 3))
        val result = deliveryReportServiceImpl.getYesterdayDeliveriesReportByDate()
        assert(result.deliveries == 2)
        assert(result.averageMinutesBetweenDeliveryStart == 3)
    }

    @Test
    fun report_scenario_created_minutes_apart_2() {
        createDeliveryObject(startedAt = createDate(minute = 0))
        createDeliveryObject(startedAt = createDate(minute = 3))
        createDeliveryObject(startedAt = createDate(minute = 10))
        val result = deliveryReportServiceImpl.getYesterdayDeliveriesReportByDate()
        assert(result.deliveries == 3)
        assert(result.averageMinutesBetweenDeliveryStart == 5)
    }

    @Test
    fun report_scenario_created_seconds_apart_2() {
        createDeliveryObject(startedAt = createDate(second = 0))
        createDeliveryObject(startedAt = createDate(second = 3))
        createDeliveryObject(startedAt = createDate(second = 10))
        val result = deliveryReportServiceImpl.getYesterdayDeliveriesReportByDate()
        assert(result.deliveries == 3)
        assert(result.averageMinutesBetweenDeliveryStart == 0)
    }

    private fun createDate(
        minusDays: Long = 1,
        hour: Int = 1,
        minute: Int = 0,
        second: Int = 0,
        zoneName: String = "Europe/Amsterdam"
    ) =
        ZonedDateTime
            .of(LocalDateTime.of(LocalDate.now(), LocalTime.of(hour, minute, second)), ZoneId.of(zoneName))
            .minusDays(minusDays)

    private fun createDeliveryObject(
        plate: String = "TES-123",
        startedAt: ZonedDateTime = createDate(),
        status: DeliveryStatus = DeliveryStatus.IN_PROGRESS
    ) = deliveryServiceImpl.createDelivery(
        CreateDelivery(
            VehicleId(plate),
            startedAt,
            status
        )
    )
}
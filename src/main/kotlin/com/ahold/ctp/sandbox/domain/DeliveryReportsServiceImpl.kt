package com.ahold.ctp.sandbox.domain

import com.ahold.ctp.sandbox.domain.ports.DeliveryReportsServiceInterface
import com.ahold.ctp.sandbox.domain.valueobjects.AverageMinutesBetweenDeliveryReport
import com.ahold.ctp.sandbox.repositories.DeliveryRepository
import com.ahold.ctp.sandbox.utils.AMSTERDAM_TIMEZONE
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.*
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

@Service
class DeliveryReportsServiceImpl(
    private val deliveryRepository: DeliveryRepository
) : DeliveryReportsServiceInterface {

    override fun getYesterdayDeliveriesReportByDate(): AverageMinutesBetweenDeliveryReport {
        val (startDate, endDate) = getStartAndEndDates()
        val deliveries = deliveryRepository.findAllByStartedAtBetweenOrderByStartedAt(startDate, endDate)

        val timeDifferences = deliveries.map { it.startedAt.time.milliseconds.inWholeMinutes }
            .zipWithNext { first, second -> abs(first - second) }

        val average = timeDifferences.takeIf { it.isNotEmpty() }
            ?.let { it.sum() / it.size }
            ?: 0

        return AverageMinutesBetweenDeliveryReport(deliveries.size, average.toInt())
    }

    private fun getStartAndEndDates(): Pair<Timestamp, Timestamp> {
        val amsterdamZone = AMSTERDAM_TIMEZONE
        val yesterday = LocalDateTime.now(amsterdamZone)
            .minus(1, ChronoUnit.DAYS)
            .toLocalDate()
        val startDate = getTimestampInUTC(yesterday, LocalTime.MIDNIGHT, amsterdamZone)
        val endDate = getTimestampInUTC(yesterday, LocalTime.MAX, amsterdamZone)
        return Pair(startDate, endDate)
    }

    private fun getTimestampInUTC(forDate: LocalDate, time: LocalTime, timeZone: ZoneId?): Timestamp =
        Timestamp.valueOf(
            LocalDateTime.of(
                LocalDate.of(forDate.year, forDate.month, forDate.dayOfMonth),
                time
            )
                .atZone(timeZone)
                .withZoneSameInstant(ZoneOffset.UTC)
                .toLocalDateTime()
        )
}

package com.ahold.ctp.sandbox.utils

import java.sql.Timestamp
import java.time.ZoneId
import java.time.ZonedDateTime

fun Timestamp.toZonedDateTimeUTC(): ZonedDateTime = ZonedDateTime.of(this.toLocalDateTime(), ZoneId.of("UTC"))
fun ZonedDateTime.toTimestamp(): Timestamp = Timestamp.valueOf(this.toLocalDateTime())
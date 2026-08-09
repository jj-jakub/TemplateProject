package com.jj.templateproject.domain.time

/**
 * Formats an epoch-millisecond instant as an ISO-8601 UTC timestamp, e.g. `2026-08-07T14:03:22Z`.
 *
 * Hand-rolled rather than reached for through a platform formatter, for two reasons: a pure function
 * is unit-testable with no Android runtime and no locale, and the output is fixed by the format
 * rather than by the device's settings, so the same instant reads the same everywhere. That matters
 * for anything that travels (a log line, an analytics parameter, an artifact file name) as opposed
 * to anything shown to a user, which should be locale-formatted instead.
 */
object IsoTimestamp {

    fun format(epochMillis: Long): String {
        val epochDay = epochMillis.floorDiv(MILLIS_PER_DAY)
        val millisOfDay = epochMillis.mod(MILLIS_PER_DAY)

        // Howard Hinnant's civil-from-days: shifting the epoch to 0000-03-01 puts the leap day at
        // the end of the year, which is what makes the month/day arithmetic below branch- and
        // table-free.
        val shiftedDay = epochDay + DAYS_FROM_SHIFTED_EPOCH
        val era = shiftedDay.floorDiv(DAYS_PER_ERA)
        val dayOfEra = shiftedDay - era * DAYS_PER_ERA
        val yearOfEra = (dayOfEra - dayOfEra / 1460 + dayOfEra / 36524 - dayOfEra / 146096) / 365
        val dayOfYear = dayOfEra - (365 * yearOfEra + yearOfEra / 4 - yearOfEra / 100)
        val marchMonth = (5 * dayOfYear + 2) / 153
        val day = dayOfYear - (153 * marchMonth + 2) / 5 + 1
        val month = if (marchMonth < 10) marchMonth + 3 else marchMonth - 9
        val year = yearOfEra + era * 400 + if (month <= 2) 1 else 0

        val secondOfDay = millisOfDay / MILLIS_PER_SECOND
        val hour = secondOfDay / SECONDS_PER_HOUR
        val minute = secondOfDay / SECONDS_PER_MINUTE % MINUTES_PER_HOUR
        val second = secondOfDay % SECONDS_PER_MINUTE

        return "${pad(year, YEAR_DIGITS)}-${pad(month)}-${pad(day)}T${pad(hour)}:${pad(minute)}:${pad(second)}Z"
    }

    private fun pad(value: Long, digits: Int = 2) = value.toString().padStart(digits, '0')

    private const val MILLIS_PER_SECOND = 1_000L
    private const val MILLIS_PER_DAY = 86_400_000L
    private const val SECONDS_PER_MINUTE = 60L
    private const val MINUTES_PER_HOUR = 60L
    private const val SECONDS_PER_HOUR = 3_600L
    private const val DAYS_PER_ERA = 146_097L
    private const val DAYS_FROM_SHIFTED_EPOCH = 719_468L
    private const val YEAR_DIGITS = 4
}

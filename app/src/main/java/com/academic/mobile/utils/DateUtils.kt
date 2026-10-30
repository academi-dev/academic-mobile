package com.academic.mobile.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Utilidades para el formateo y conversión de fechas ISO 8601 en UTC utilizadas por `academic-api`.
 */
object DateUtils {

    private val LOCALE_ES = Locale("es", "ES")

    private val DISPLAY_FORMATTER: DateTimeFormatter =
        DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", LOCALE_ES)

    private val ISO_UTC_FORMATTER: DateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
            .withZone(ZoneOffset.UTC)

    /**
     * Convierte una cadena de fecha ISO 8601 (con o sin zona horaria) a un formato legible en español.
     * Si la cadena es nula o inválida, devuelve [fallback].
     */
    fun formatIsoToDisplay(isoString: String?, fallback: String = "Sin fecha de entrega"): String {
        if (isoString.isNullOrBlank()) return fallback
        val trimmed = isoString.trim()

        return try {
            val instant = parseToInstant(trimmed)
            val zoned = instant.atZone(ZoneOffset.UTC)
            DISPLAY_FORMATTER.format(zoned)
        } catch (_: Exception) {
            fallback
        }
    }

    /**
     * Construye una cadena ISO 8601 en UTC (`yyyy-MM-dd'T'HH:mm:ss'Z'`) a partir de los milisegundos
     * UTC seleccionados en `MaterialDatePicker` y la hora/minuto de `MaterialTimePicker`.
     */
    fun buildIsoUtcString(dateMillisUtc: Long, hour: Int, minute: Int): String {
        val baseDate = Instant.ofEpochMilli(dateMillisUtc)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()
        val dateTimeUtc = baseDate
            .atTime(hour.coerceIn(0, 23), minute.coerceIn(0, 59))
            .atZone(ZoneOffset.UTC)
        return ISO_UTC_FORMATTER.format(dateTimeUtc)
    }

    /**
     * Convierte una cadena ISO 8601 en un [Instant] soportando sufijos `Z`, desplazamientos (`+00:00`)
     * o fechas locales sin zona horaria explícita (asumidas como UTC).
     */
    fun parseToInstant(isoString: String): Instant {
        return try {
            Instant.parse(isoString)
        } catch (_: Exception) {
            try {
                OffsetDateTime.parse(isoString).toInstant()
            } catch (_: Exception) {
                LocalDateTime.parse(isoString).toInstant(ZoneOffset.UTC)
            }
        }
    }

    /**
     * Formatea un timestamp en milisegundos junto con hora y minuto para mostrarlo en el formulario.
     */
    fun formatPickerSelection(dateMillisUtc: Long, hour: Int, minute: Int): String {
        val iso = buildIsoUtcString(dateMillisUtc, hour, minute)
        return formatIsoToDisplay(iso)
    }
}

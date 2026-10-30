package com.academic.mobile

import com.academic.mobile.utils.DateUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pruebas unitarias para el parseo y formateo de fechas ISO 8601 UTC en [DateUtils].
 */
class DateUtilsTest {

    @Test
    fun `buildIsoUtcString genera cadena ISO 8601 en UTC con sufijo Z`() {
        // 2026-10-30T00:00:00Z en epoch millis = 1793318400000L
        val epochMillis = 1793318400000L
        val iso = DateUtils.buildIsoUtcString(epochMillis, 14, 30)
        assertEquals("2026-10-30T14:30:00Z", iso)
    }

    @Test
    fun `formatIsoToDisplay devuelve fallback cuando la fecha es nula o invalida`() {
        assertEquals(
            "Sin fecha",
            DateUtils.formatIsoToDisplay(null, fallback = "Sin fecha")
        )
        assertEquals(
            "Sin fecha",
            DateUtils.formatIsoToDisplay("fecha-invalida", fallback = "Sin fecha")
        )
    }

    @Test
    fun `formatIsoToDisplay soporta formato ISO con Z y sin zona horaria`() {
        val formattedWithZ = DateUtils.formatIsoToDisplay("2026-10-30T18:45:00Z")
        val formattedWithoutZone = DateUtils.formatIsoToDisplay("2026-10-30T18:45:00")
        assertTrue(formattedWithZ.contains("2026"))
        assertTrue(formattedWithZ.contains("18:45"))
        assertEquals(formattedWithZ, formattedWithoutZone)
    }
}

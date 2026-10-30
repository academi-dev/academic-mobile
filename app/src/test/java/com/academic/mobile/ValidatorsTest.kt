package com.academic.mobile

import com.academic.mobile.utils.Validators
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Pruebas unitarias para las funciones de validación de formularios en [Validators].
 */
class ValidatorsTest {

    @Test
    fun `validateEmail acepta correos validos`() {
        assertNull(Validators.validateEmail("estudiante@universidad.edu"))
        assertNull(Validators.validateEmail("  jean.mena@academic.dev  "))
    }

    @Test
    fun `validateEmail rechaza correos vacios o mal formados`() {
        assertNotNull(Validators.validateEmail(""))
        assertNotNull(Validators.validateEmail("   "))
        assertNotNull(Validators.validateEmail("correo-sin-arroba.com"))
        assertNotNull(Validators.validateEmail("usuario@dominio"))
    }

    @Test
    fun `validatePassword exige minimo 6 caracteres`() {
        assertNotNull(Validators.validatePassword(null))
        assertNotNull(Validators.validatePassword("12345"))
        assertNull(Validators.validatePassword("123456"))
        assertNull(Validators.validatePassword("ClaveSegura2026"))
    }

    @Test
    fun `validateNombre y validateTituloTarea exigen al menos 2 caracteres`() {
        assertNotNull(Validators.validateNombre(""))
        assertNotNull(Validators.validateNombre("A"))
        assertNull(Validators.validateNombre("Jean Mena"))

        assertNotNull(Validators.validateTituloTarea(" "))
        assertNotNull(Validators.validateTituloTarea("X"))
        assertNull(Validators.validateTituloTarea("Entrega de laboratorio 1"))
    }

    @Test
    fun `validateCodigoMateria y validateSemestre validan campos requeridos`() {
        assertEquals(
            "El código de la materia es obligatorio.",
            Validators.validateCodigoMateria("")
        )
        assertNull(Validators.validateCodigoMateria("MAT101"))

        assertNotNull(Validators.validateSemestre(" "))
        assertNull(Validators.validateSemestre("2026-1"))
    }
}

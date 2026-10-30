package com.academic.mobile

import com.academic.mobile.data.api.ApiErrorParser
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Pruebas unitarias para el parser del formato de error estándar de `academic-api` en [ApiErrorParser].
 */
class ApiErrorParserTest {

    @Test
    fun `parseErrorJson extrae el mensaje del sobre de error estandar`() {
        val json = """
            {
              "error": {
                "codigo": "CREDENCIALES_INVALIDAS",
                "mensaje": "Correo o contraseña incorrectos"
              }
            }
        """.trimIndent()

        val mensaje = ApiErrorParser.parseErrorJson(json, httpCode = 401)
        assertEquals("Correo o contraseña incorrectos", mensaje)
    }

    @Test
    fun `parseErrorJson usa mensaje por defecto segun codigo HTTP si el cuerpo no es JSON valido`() {
        assertEquals(
            "Credenciales inválidas o sesión expirada.",
            ApiErrorParser.parseErrorJson(null, httpCode = 401)
        )
        assertEquals(
            "El recurso solicitado no fue encontrado.",
            ApiErrorParser.parseErrorJson("<html>404</html>", httpCode = 404)
        )
        assertEquals(
            "El registro ya existe o genera un conflicto.",
            ApiErrorParser.parseErrorJson("{}", httpCode = 409)
        )
    }
}
